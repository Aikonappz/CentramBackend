import { SelectionModel } from '@angular/cdk/collections';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { Incident } from '../../model/Incident';
import { LoggedInUser } from '../../model/LoggedInUser';
import { Permission } from '../../model/Permssion';
import { Priority, PriorityList } from '../../model/Priority';
import { UserVO, UserVOListResponse } from '../../model/UserVO';
import { IncomingIncidentDataSource } from '../../service/datasource/IncomingIncidentDataSource';
import { IncidentService } from '../../service/IncidentService';
import { MiscService } from '../../service/MiscService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { UserService } from '../../service/UserService';
import { AssignUser } from '../../model/AssignUser';
declare var $: any;

@Component({
  selector: 'app-incomingincident',
  templateUrl: './incomingincident.component.html',
  styleUrls: ['./incomingincident.component.scss']
})
export class IncomingIncidentComponent implements OnInit {
  displayedColumns = ['select', 'incDtl', 'assignedUser', 'slaAt', 'status', 'action'];
  datasource: IncomingIncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  selection = new SelectionModel<Incident>(true, []);
  statusList: string[];
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  angForm: FormGroup;
  agentList: UserVO[] = [];
  userList: UserVO[] = [];
  priorities: Priority[] = [];
  loggedInUser: LoggedInUser;
  moduleIds: number[] = [];
  canAssignNow: boolean = false;
  //selectedValues: Set<IncidentVO> = new Set<IncidentVO>();
  selectedValues: Map<number, string> = new Map<number, string>();
  modalRef: BsModalRef;
  searchedData: Object = {};
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: IncidentService,
    private userService: UserService,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });

    this.angForm = this.fb.group({
      incidentNo: new FormControl(null, [
      ]),
      moduleId: new FormControl(null, [
      ]),
      subModuleId: new FormControl(null, [
      ]),
      priorityId: new FormControl(null, [
      ]),
      raisedUser: new FormControl(null, [
      ]),
      assignedUser: new FormControl(null, [
      ]),
      status: new FormControl(null, [
      ]),
      title: new FormControl('', [
      ]),
    });

    this.userService.getUsersService({})
      .subscribe((data: UserVOListResponse) => {
        for (let i in data.content) {
          if (
            this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD')
            ||
            this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')
          ) {
            if (this.confirmAgentRole(data.content[i].roleNames))
              this.agentList.push(data.content[i]);
          } else {
            this.loggedInUser = this.loggedInUserService.getLoggedInUser();
            if (this.loggedInUser.userId == data.content[i].id)
              this.agentList.push(data.content[i]);
          }
          if (this.confirmUserRole(data.content[i].roleNames)) {
            this.userList.push(data.content[i]);
          }
        }
        //console.log(this.tmpuserList);
        //console.log(this.tmpagentList);
      });
    this.miscService.prioritiesService({})
      .subscribe((result: PriorityList) => {
        this.priorities = result.content;
      });
    this.statusList = Object.values(IncidentStatus)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));

    this.permissions = this.loggedInUserService.getModulePermissions();
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null) {
        this.moduleList.push(this.permissions[i]);
      }
    }

    this.selection.changed.subscribe(i => {
      for (let k = 0; k < i.added.length; k++) {
        let obj = i.added[k];
        this.selectedValues.set(obj.id, obj.incidentNo);
      }
      for (let k = 0; k < i.removed.length; k++) {
        let obj = i.removed[k];
        this.selectedValues.delete(obj.id);
      }
      //console.log(this.selectedValues);
    });
  }

  private confirmAgentRole(roles: string[]): boolean {
    for (let k in roles) {
      if (roles[k].match(/.*_AGENT_.*/)) {
        return true;
      }
    }
    return false;
  }

  private confirmUserRole(roles: string[]): boolean {
    for (let k in roles) {
      if (roles[k].match(/.*_USER_.*/)) {
        return true;
      }
    }
    return false;
  }

  getTitle(state, parent) {
    var data = [];
    if (parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }
    if (state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  ngOnInit(): void {
    this.datasource = new IncomingIncidentDataSource(this.service);
    this.datasource.loadData();
  }

  ngAfterViewInit() {
    this.datasource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();
    this.paginator.page
      .pipe(
        tap(() => this.loadData())
      )
      .subscribe();
    this.canAssignNow = false;
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  edit(inc: Incident) {
    this.router.navigate(['/incident/edit/' + inc.id]);
  }
  add() {
    this.router.navigate(['/incident/raise']);
  }

  loadData(req = {}) {
    //console.log(req);
    if (this.searchedData.hasOwnProperty('incidentNo')) {
      req = this.searchedData;
    }
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  loadPage() {
    this.angForm.reset();
    this.searchedData = {};
    this.loadData({});
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      let title = this.angForm.controls['title'].value;
      let status = this.angForm.controls['status'].value;
      let assignedUserId = this.angForm.controls['assignedUser'].value;
      let priorityId = this.angForm.controls['priorityId'].value;
      let subModuleId = this.angForm.controls['subModuleId'].value;
      let moduleId = this.angForm.controls['moduleId'].value;
      let incidentNo = this.angForm.controls['incidentNo'].value;
      this.searchedData = {
        "incidentNo": incidentNo == null ? '' : incidentNo,
        "title": title == null ? '' : title,
        "status": status == null ? '' : status,
        "assignedUserId": status == null ? '' : assignedUserId,
        "priorityId": status == null ? '' : priorityId,
        "subModuleId": subModuleId == null ? '' : subModuleId,
        "moduleId": moduleId == null ? '' : moduleId,
      };
      this.loadData(this.searchedData);
      // console.log({
      //   "title": title == null ? '' : title,
      //   "status": status == null ? '' : status,
      //   "assignedUserId": status == null ? '' : assignedUserId,
      //   "priorityId": status == null ? '' : priorityId,
      //   "subModuleId": subModuleId == null ? '' : subModuleId,
      //   "moduleId": moduleId == null ? '' : moduleId,
      // });
      this.selection.clear();
      //console.log(moduleId);
      //console.log(subModuleId);
      if (subModuleId != null && subModuleId.replace(/\s/g, "") != ""
        && moduleId != null && moduleId.replace(/\s/g, "") != "") {
        this.canAssignNow = true;
      } else {
        this.canAssignNow = false;
      }
    } else {
      console.log("Invalid Form!");
    }
  }

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    let c = 0;
    if (moduleId != "") {
      this.subModuleList = [];
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].moduleParentId == moduleId) {
          this.subModuleList[c] = this.permissions[i];
          c++;
        }
      }
      this.moduleIds.push(moduleId);
      let params = {
        "moduleIds": this.moduleIds.join(','),
        "actionName": 'SOLVE',
      };
      this.agentList = [];
      this.userService
        .getUsersByModuleAndAction(params)
        .subscribe((data: UserVO[]) => {
          for (let i = 0; i < data.length; i++) {
            if (
              this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD')
              ||
              this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')
            ) {
              if (this.confirmAgentRole(data[i].roleNames))
                this.agentList.push(data[i]);
            } else {
              this.loggedInUser = this.loggedInUserService.getLoggedInUser();
              if (this.loggedInUser.userId == data[i].id)
                this.agentList.push(data[i]);
            }
            //this.agentList.push(data[i]);
          }
          //console.log(data);
        });
    }
  }

  @ViewChild("subModuleId") subModuleId;
  populateUser(subModuleId) {
    let c = 0;
    if (subModuleId != "") {
      this.moduleIds.push(subModuleId);
      let params = {
        "moduleIds": this.moduleIds.join(','),
        "actionName": 'SOLVE',
      };
      this.agentList = [];
      this.userService
        .getUsersByModuleAndAction(params)
        .subscribe((data: UserVO[]) => {
          for (let i = 0; i < data.length; i++) {
            if (
              this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD')
              ||
              this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')
            ) {
              if (this.confirmAgentRole(data[i].roleNames))
                this.agentList.push(data[i]);
            } else {
              this.loggedInUser = this.loggedInUserService.getLoggedInUser();
              if (this.loggedInUser.userId == data[i].id)
                this.agentList.push(data[i]);
            }
            //this.agentList.push(data[i]);
          }
          //console.log(data);
        });
    }
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.datasource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.datasource.data.forEach(row => {
        //console.log(row);
        this.selection.select(row);
      });
  }

  logSelection() {
    this.selection.selected.forEach(s => console.log(s.id));
  }

  assign(inc: Incident) {
    let moduleIds = [];
    moduleIds.push(inc.moduleId);
    moduleIds.push(inc.subModuleId);
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    let params = {
      "moduleIds": moduleIds,
      "actionName": 'SOLVE',
    };
    this.agentList = [];
    this.userService
      .getUsersByModuleAndAction(params)
      .subscribe((data: UserVO[]) => {
        for (let i = 0; i < data.length; i++) {
          if (
            this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD')
            ||
            this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')
          ) {
            if (this.confirmAgentRole(data[i].roleNames))
              this.agentList.push(data[i]);
          } else {
            if (this.loggedInUser.userId == data[i].id)
              this.agentList.push(data[i]);
          }
        }
        this.selection.clear();
        this.selection.select(inc);
        this.canAssignNow = true;
        this.openAssignModal();
        //console.log(data);
      });
  }

  openAssignModal() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState: Partial<AssignUser> = {
      agentList: this.agentList,
      canAssign: (this.canAssignNow && this.selectedValues.size > 0) ? true : false,
      selectedValues: this.selectedValues
    };
    this.modalRef = this.modalService.show(AssignIncidentComponent,
      Object.assign({}, config, { initialState })
    );
  }
}

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-male"></i> Assign Incident To User {{canAssign}}</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
      <div class="col-sm-12">
          <div class="card ">
              <div [ngClass]="{'d-none': canAssign === true, 'card-body' : true }">
                  <div class="row">
                      <h6>Please search with module, submodule and select incident to assign an user!</h6>
                  </div>
              </div>
              <form [ngClass]="{'d-none': canAssign === false}" [formGroup]="angFormAssign"
                  (ngSubmit)="assignIncident()" novalidate>
                  <div class="card-body">
                      <div class="row">
                          <div class="col">
                              <label class="form-col-form-label required-control-label" for="assignUser">Agent</label>
                              <select class="form-control" formControlName="assignUser" id="assignUser"
                                  name="assignUser">                                  
                                  <option *ngFor="let e of agentList" value="{{e.id}}">{{e.email}}</option>
                              </select>
                              <div *ngIf="uf.assignUser.touched && uf.assignUser.invalid"
                                  class="alert alert-danger-custom">
                                  <div *ngIf="uf.assignUser.errors?.required">
                                      Please select agent to assign.
                                  </div>
                              </div>
                          </div>
                          <div class="col">
                            <input type="hidden" [(ngModel)]="incidents" formControlName="incidents" ngModel id="incidents" name="incidents">
                              <label class="form-col-form-label required-control-label" for="incidents">Incident</label>
                              <textarea formControlName="selectedIncidents" id="selectedIncidents"
                              name="selectedIncidents" readonly>
                              </textarea>                              
                              <div *ngIf="uf.incidents.touched && uf.incidents.invalid"
                                  class="alert alert-danger-custom">
                                  <div *ngIf="uf.incidents.errors?.required">
                                      Please select incident.
                                  </div>
                              </div>
                          </div>
                      </div>
                  </div>
                  <div class="card-footer">
                      <button [disabled]="!angFormAssign.valid" type="submit" class="btn btn-primary btn-sm">
                          <i class="fa fa-male"></i> Assign
                      </button>
                      <button type="button" (click)="bsModalRef.hide()" class="btn btn-danger btn-sm">
                          <i class="fa fa-close"></i> Cancel
                      </button>
                  </div>
              </form>
          </div>
      </div>
  </div>
</div>`
})
export class AssignIncidentComponent implements OnInit {
  agentList: UserVO[];
  canAssign: boolean;
  selectedValues: Map<number, string>;
  assignUser: Partial<AssignUser>;
  angFormAssign: FormGroup;
  incidents: number[] = [];
  incidentNos: string[] = [];
  selectedIncidents: string;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: IncidentService,
    public options: ModalOptions,
  ) {
    this.angFormAssign = this.fb.group({
      assignUser: new FormControl(null, [
        Validators.required,
      ]),
      incidents: new FormControl(null, [
        Validators.required,
      ]),
      selectedIncidents: new FormControl(null, [
        //Validators.required,
      ]),
    });
    this.assignUser = this.options.initialState.valueOf();
    //console.log((this.assignUser));
    this.assignUser.selectedValues.forEach((value: string, key: number) => {
      this.incidents.push(key);
      this.incidentNos.push(value);
    });
    this.selectedIncidents = this.incidentNos.join('\n');
    //console.log(this.incidents);
  }
  ngOnInit() {
    if (this.assignUser.agentList.length == 1) {
      this.angFormAssign.get('assignUser').setValue(this.assignUser.agentList[0].id);
    }
    this.angFormAssign.get('incidents').setValue(this.incidents);
    this.angFormAssign.get('selectedIncidents').setValue(this.selectedIncidents);
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get uf() { return this.angFormAssign.controls; }

  callAssignUser(req: any) {
    this.service.assignIncidentService(req.ids, req.userId, {})
      .subscribe((data: any) => {
        console.log("completed....");
        this.bsModalRef.hide();
      });
  }

  assignIncident() {
    if (this.angFormAssign.valid) {
      //console.log(formData);
      let assignUser = this.angFormAssign.controls['assignUser'].value;
      let incidents = this.angFormAssign.controls['incidents'].value;
      //console.log({ userId: assignUser, ids: incidents });
      this.callAssignUser({ userId: assignUser, ids: incidents });
    } else {
      console.log("Invalid Form!");
    }
  }
}
