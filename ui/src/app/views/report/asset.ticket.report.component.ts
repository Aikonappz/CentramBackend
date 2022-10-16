import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { LoggedInUser } from '../../model/LoggedInUser';
import { Permission } from '../../model/Permssion';
import { Priority, PriorityList } from '../../model/Priority';
import { UserVO, UserVOListResponse } from '../../model/UserVO';
import { AssetIncidentReportDataSource } from '../../service/datasource/AssetIncidentReportDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { ReportService } from '../../service/ReportService';
import { UserService } from '../../service/UserService';

@Component({
  selector: 'app-asset-ticket-report',
  templateUrl: './asset.ticket.report.component.html',
  styleUrls: ['./asset.ticket.report.component.scss']
})
export class AssetTicketReportComponent implements OnInit {
  moduleName: string = "ASSET TICKET REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['incDtl', 'assetDtl', 'slaAt', 'assignedUser', 'status',];
  datasource: AssetIncidentReportDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: any[] = [];
  angForm: FormGroup;
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  agentList: UserVO[] = [];
  userList: UserVO[] = [];
  priorities: Priority[] = [];
  loggedInUser: LoggedInUser;
  moduleIds: number[] = [];
  canAssignNow: boolean = false;
  searchedData: any = { incidentType: "ASSET", approved: -1, };
  booleanList: any[] = [];
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: ReportService,
    private userService: UserService,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
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
    this.booleanList.push({ id: 1, label: 'Deallocated' });
    this.booleanList.push({ id: 0, label: 'Allocated' });
    this.angForm = this.fb.group({
      incidentNo: new FormControl('', [
      ]),
      moduleId: new FormControl(null, [
        //Validators.required,
      ]),
      subModuleId: new FormControl(null, [
        //Validators.required,
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
    this.miscService.prioritiesService({ "priorityType": "ASSET", "sort": "name,asc" })
      .subscribe((result: PriorityList) => {
        this.priorities = result.content;
      });
    this.permissions = this.loggedInUserService.getModulePermissions();
    let p;
    this.moduleList = [];
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == "ASSET") {
        p = new Permission(this.permissions[i]);
        p.customerModuleName = p.customerModuleName;
        //AppUtility.toTitleCase(p.customerModuleName);
        this.moduleList.push(p);
      }
    }
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

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
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
    this.datasource = new AssetIncidentReportDataSource(this.service);
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
  }

  ngAfterContentInit() {
  }

  get f() { return this.angForm.controls; }

  download() {
    this.service
      .downloadAssetReport(this.searchedData)
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: "text/csv" });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          alert('Please disable your Pop-up blocker and try again.');
        }
      });
  }

  loadData(req = {}) {
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
    this.searchedData = { incidentType: "ASSET", approved: -1, };
    this.loadData(this.searchedData);
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
        this.searchedData.deallocated = 0;
        this.searchedData.assigned = 1;
      } else if (alocationStatus == 1) {
        this.searchedData.assigned = -1;
        this.searchedData.deallocated = 1;
      } else {
        this.searchedData.assigned = -1;
        this.searchedData.deallocated = -1;
      }
      this.loadData(this.searchedData);
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
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
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

}