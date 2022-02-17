import { SelectionModel } from '@angular/cdk/collections';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { Incident } from '../../model/Incident';
import { LoggedInUser } from '../../model/LoggedInUser';
import { Permission } from '../../model/Permssion';
import { Priority, PriorityList } from '../../model/Priority';
import { UserVO } from '../../model/UserVO';
import { IncomingIncidentDataSource } from '../../service/datasource/IncomingIncidentDataSource';
import { IncidentService } from '../../service/IncidentService';
import { MiscService } from '../../service/MiscService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { UserService } from '../../service/UserService';
declare var $: any;

@Component({
  selector: 'app-assignedincident',
  templateUrl: './assignedincident.component.html',
  styleUrls: ['./assignedincident.component.scss']
})
export class AssignedIncidentComponent implements OnInit {
  moduleName: string = "MY GROUP INCIDENTS";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['incDtl', 'slaAt', 'assignedUser', 'status', 'action'];
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
  searchedData: Object = {};
  incidentStatusMarker: Map<IncidentStatus, string>;
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
    this.angForm = this.fb.group({
      incidentNo: new FormControl(null, [
      ]),
      moduleId: new FormControl(null, [
      ]),
      subModuleId: new FormControl(null, [
      ]),
      priorityId: new FormControl(null, [
      ]),
      // raisedUser: new FormControl(null, [
      // ]),
      // assignedUser: new FormControl(null, [
      // ]),
      status: new FormControl(null, [
      ]),
      title: new FormControl('', [
      ]),
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    // this.userService.getUsersService({})
    //   .subscribe((data: UserVOListResponse) => {
    //     for (let i in data.content) {
    //       if (this.loggedInUser.userId == data.content[i].id)
    //         this.agentList.push(data.content[i]);
    //       if (this.confirmUserRole(data.content[i].roleNames)) {
    //         this.userList.push(data.content[i]);
    //       }
    //     }
    //     //console.log(this.tmpuserList);
    //     //console.log(this.tmpagentList);
    //   });
    this.miscService.prioritiesService({ "sort": "name,asc" })
      .subscribe((result: PriorityList) => {
        this.priorities = result.content;
      });
    this.permissions = this.loggedInUserService.getModulePermissions();
    let p;
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == "INCIDENT") {
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
    this.route.params.subscribe(params => {
      let type = this.route.snapshot.paramMap.get('dp');
      //console.log(type);
    });
    this.datasource = new IncomingIncidentDataSource(this.service);
    this.searchedData = {
      "incidentNo": '',
      "assignedUserId": this.loggedInUser.userId,
    };
    this.datasource.loadData(
      0, 10, this.searchedData
    );
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
    this.router.navigate(['/incident/agent-mine/edit/' + inc.id]);
  }
  add() {
    this.router.navigate(['/incident/agent-mine/add']);
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
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
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
      let priorityId = this.angForm.controls['priorityId'].value;
      let subModuleId = this.angForm.controls['subModuleId'].value;
      let moduleId = this.angForm.controls['moduleId'].value;
      let incidentNo = this.angForm.controls['incidentNo'].value;
      this.searchedData = {
        "incidentNo": incidentNo == null ? '' : incidentNo,
        "title": title == null ? '' : title,
        "status": status == null ? '' : status,
        "assignedUserId": this.loggedInUser.userId,
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
      this.moduleIds = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == moduleId && this.permissions[i].licenseType == "INCIDENT") {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
      this.moduleIds.push(moduleId);
      let params = {
        "moduleIds": this.moduleIds.join(','),
        "actionName": 'SOLVE',
      };
      this.agentList = [];
      // this.userService
      //   .getUsersByModuleAndAction(params)
      //   .subscribe((data: UserVO[]) => {
      //     for (let i = 0; i < data.length; i++) {
      //       if (this.loggedInUser.userId == data[i].id)
      //         this.agentList.push(data[i]);
      //       //this.agentList.push(data[i]);
      //     }
      //     //console.log(data);
      //   });
    }
  }

  @ViewChild("subModuleId") subModuleId;
  populateUser(subModuleId) {
    let c = 0;
    if (subModuleId != "") {
      let moduleId = this.moduleIds[0];
      this.moduleIds = [];
      this.moduleIds.push(moduleId);
      this.moduleIds.push(subModuleId);
      let params = {
        "moduleIds": this.moduleIds.join(','),
        "actionName": 'SOLVE',
      };
      this.agentList = [];
      // this.userService
      //   .getUsersByModuleAndAction(params)
      //   .subscribe((data: UserVO[]) => {
      //     for (let i = 0; i < data.length; i++) {
      //       if (this.loggedInUser.userId == data[i].id)
      //         this.agentList.push(data[i]);
      //       //this.agentList.push(data[i]);
      //     }
      //     //console.log(data);
      //   });
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
}