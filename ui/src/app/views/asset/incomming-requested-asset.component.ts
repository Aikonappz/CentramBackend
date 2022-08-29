import { SelectionModel } from '@angular/cdk/collections';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
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
import { IAssignUser } from '../../model/IAssignUser';
import { DeallocateAsset } from './modal/DeallocateAsset';
import { AssignIncidentComponent } from './modal/AssignIncidentComponent';
declare var $: any;

@Component({
  selector: 'app-incomming-requested-asset',
  templateUrl: './incomming-requested-asset.component.html',
  styleUrls: ['./incomming-requested-asset.component.scss']
})
export class IncommingRequestedAssetComponent implements OnInit {
  moduleName: string = "REQUESTED ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['select', 'incDtl', 'assetDtl', 'slaAt', 'assignedUser', 'status', 'action'];
  datasource: IncomingIncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  selection = new SelectionModel<Incident>(true, []);
  statusList: any = [];
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
  selectedValues: Map<number, string> = new Map<number, string>();
  modalRef: BsModalRef;
  searchedData: any = { incidentType: "ASSET", approved: 1 };
  booleanList: any[] = [];
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: IncidentService,
    private userService: UserService,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private modalService: BsModalService,
    private route: ActivatedRoute,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    for (let item in IncidentStatus) {
      if (item != "ALL") {
        this.statusList.push({ "key": item, "value": IncidentStatus[item] });
      }
    }
    this.statusList.sort(function (a, b) {
      if (b.key > a.key) return -1;
      if (a.key > b.key) return 1;
      return 0;
    });
    this.booleanList.push({ id: 0, label: 'Deallocated' });
    this.booleanList.push({ id: 1, label: 'Allocated' });
    this.angForm = this.fb.group({
      incidentNo: new FormControl('', [
      ]),
      moduleId: new FormControl(null, [
        Validators.required,
      ]),
      subModuleId: new FormControl(null, [
        Validators.required,
      ]),
      priorityId: new FormControl(null, [
      ]),
      raisedUser: new FormControl('', [
      ]),
      assignedUser: new FormControl(null, [
      ]),
      status: new FormControl(null, [
      ]),
      title: new FormControl('', [
      ]),
      serialNo: new FormControl('', [
      ]),
      alocationStatus: new FormControl(null, [
      ]),
    });
    this.userService.getUsersService({})
      .subscribe((data: UserVOListResponse) => {
        for (let i in data.content) {
          if (
            this.loggedInUserService.hasRole('ORG_ASSET_AGENT_LEAD')
            ||
            this.loggedInUserService.hasRole('ORG_ASSET_AGENT_MANAGER')
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
    this.miscService.prioritiesService({  "priorityType": "ASSET","sort": "name,asc" })
      .subscribe((result: PriorityList) => {
        this.priorities = result.content;
      });
    this.permissions = this.loggedInUserService.getModulePermissions();
    let p;
    this.moduleList = [];
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == "ASSET") {
        p = new Permission(this.permissions[i]);
        p.customerModuleName = AppUtility.toTitleCase(p.customerModuleName);
        this.moduleList.push(p);
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

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
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
    // this.route.params.subscribe(params => {
    //   let type = this.route.snapshot.paramMap.get('dp');
    //   //console.log(type);
    // });
    this.datasource = new IncomingIncidentDataSource(this.service);
    this.datasource.loadData(0, 10, this.searchedData);
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

  get f() { return this.angForm.controls; }

  edit(inc: Incident) {
    this.router.navigate(['/asset/agent-all/edit/' + inc.id]);
  }
  add() {
    this.router.navigate(['/asset/agent-all/add']);
  }

  loadData(req?: any) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, this.searchedData);
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  loadPage() {
    this.angForm.reset();
    this.searchedData = { incidentType: "ASSET", approved: 1 };
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
      let alocationStatus = this.angForm.controls['alocationStatus'].value;
      let serialNo = this.angForm.controls['serialNo'].value;
      this.searchedData.incidentNo = incidentNo == null ? '' : incidentNo;
      this.searchedData.title = title == null ? '' : title;
      this.searchedData.status = status == null ? '' : status;
      this.searchedData.assignedUserId = assignedUserId == null ? '' : assignedUserId;
      this.searchedData.priorityId = priorityId == null ? '' : priorityId;
      this.searchedData.subModuleId = subModuleId == null ? '' : subModuleId;
      this.searchedData.moduleId = moduleId == null ? '' : moduleId;
      this.searchedData.serialNo = serialNo == null ? '' : serialNo;
      if (alocationStatus == 0) {
        this.searchedData.deallocated = 1;
        this.searchedData.assigned = -1;
      } else if (alocationStatus == 1) {
        this.searchedData.assigned = 1;
        this.searchedData.deallocated = -1;
      } else {
        this.searchedData.assigned = -1;
        this.searchedData.deallocated = -1;
      }
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
      // console.log(moduleId);
      // console.log(subModuleId);
      // if (
      //   subModuleId != null && subModuleId.replace(/\s/g, "") != ""
      //   &&
      //   moduleId != null && moduleId.replace(/\s/g, "") != ""
      // ) {
      //   this.canAssignNow = true;
      // } else {
      //   this.canAssignNow = false;
      // }
      if (subModuleId != null && moduleId != null) {
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
    if (typeof moduleId !== 'undefined') {
      let c = 0;
      this.subModuleList = [];
      this.moduleIds = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == moduleId.moduleId && this.permissions[i].licenseType == "ASSET") {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
      this.moduleIds.push(moduleId.moduleId);
      let params = {
        "moduleIds": this.moduleIds.join(','),
        "actionName": 'SOLVE',
      };
      this.agentList = [];
      this.userService
        .getUsersByModuleAndAction(params)
        .subscribe((data: UserVO[]) => {
          this.agentList = [];
          for (let i = 0; i < data.length; i++) {
            if (
              this.loggedInUserService.hasRole('ORG_ASSET_AGENT_LEAD')
              ||
              this.loggedInUserService.hasRole('ORG_ASSET_AGENT_MANAGER')
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
      this.angForm.controls['subModuleId'].setValue(null);
      this.angForm.controls['assignedUser'].setValue(null);
    } else {
      this.angForm.controls['subModuleId'].setValue(null);
      this.angForm.controls['assignedUser'].setValue(null);
    }
  }

  @ViewChild("subModuleId") subModuleId;
  populateUser(subModuleId) {
    let c = 0;
    if (typeof subModuleId !== 'undefined') {
      let moduleId = this.moduleIds[0];
      this.moduleIds = [];
      this.moduleIds.push(moduleId);
      this.moduleIds.push(subModuleId.moduleId);
      let params = {
        "moduleIds": this.moduleIds.join(','),
        "actionName": 'SOLVE',
      };
      this.agentList = [];
      this.userService
        .getUsersByModuleAndAction(params)
        .subscribe((data: UserVO[]) => {
          this.agentList = [];
          for (let i = 0; i < data.length; i++) {
            if (
              this.loggedInUserService.hasRole('ORG_ASSET_AGENT_LEAD')
              ||
              this.loggedInUserService.hasRole('ORG_ASSET_AGENT_MANAGER')
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
      this.angForm.controls['assignedUser'].setValue(null);
    } else {
      this.angForm.controls['assignedUser'].setValue(null);
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

  deallocate(inc: Incident) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      incident: inc
    };
    this.modalRef = this.modalService.show(DeallocateAsset,
      Object.assign({}, config, { initialState })
    );
  }

  openAssignModal() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState: Partial<IAssignUser> = {
      agentList: this.agentList,
      canAssign: (this.canAssignNow && this.selectedValues.size > 0) ? true : false,
      selectedValues: this.selectedValues
    };
    this.modalRef = this.modalService.show(AssignIncidentComponent,
      Object.assign({}, config, { initialState })
    );
  }
}