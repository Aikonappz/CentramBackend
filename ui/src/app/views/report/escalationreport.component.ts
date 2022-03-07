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
import { Priority, PriorityList } from '../../model/Priority';
import { ReportEscalationIncidentDataSource } from '../../service/datasource/ReportEscalationIncidentDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { ReportService } from '../../service/ReportService';
import { UserService } from '../../service/UserService';
import { StartEndDateValidation } from '../../validator/StartEndDateValidation';

@Component({
  selector: 'app-escalationreport',
  templateUrl: './escalationreport.component.html',
  styleUrls: ['./escalationreport.component.scss']
})
export class EscalationReportComponent implements OnInit {
  moduleName: string = "ESCALATION REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['incDtl', 'slaAt', 'assignedUser', 'status',];
  datasource: ReportEscalationIncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: any = [];
  allModuleList: any[] = [];
  moduleList: any[] = [];
  subModuleList: any[] = [];
  angForm: FormGroup;
  priorities: Priority[] = [];
  loggedInUser: LoggedInUser;
  searchedData: Object = {};
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
    this.angForm = this.fb.group({
      moduleId: new FormControl(null, []),
      subModuleId: new FormControl(null, []),
      priorityId: new FormControl(null, []),
      status: new FormControl(null, []),
      start: new FormControl(moment().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
      ]),
      end: new FormControl(moment().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
      ]),
    }, {
      validators: StartEndDateValidation('start', 'end')
    });
    this.miscService.prioritiesService({ "sort": "name,asc" })
      .subscribe((result: PriorityList) => {
        this.priorities = result.content;
      });

    this.miscService.modulesService({ licenseType: 'ASSET', "sort": "name,asc" })
      .subscribe((result: any) => {
        this.allModuleList = result.content;
        this.moduleList = [];
        let m;
        for (let i in this.allModuleList) {
          if (this.allModuleList[i].appModule == false && this.allModuleList[i].parentModuleId == null) {
            if (this.loggedInUserService.isAgentLead() || this.loggedInUserService.isOrgAdmin() || this.loggedInUserService.isAgentManager()) {
              m = this.allModuleList[i];
              m.customerModuleName = AppUtility.toTitleCase(m.customerModuleName);
              this.moduleList.push(m);
            } else if (this.loggedInUserService.hasPermissionById(this.allModuleList[i].id, 'READ') || this.loggedInUserService.hasPermissionById(this.allModuleList[i].id, 'SOLVE')) {
              m = this.allModuleList[i];
              m.customerModuleName = AppUtility.toTitleCase(m.customerModuleName);
              this.moduleList.push(m);
            }
          }
        }
      });
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
    this.datasource = new ReportEscalationIncidentDataSource(this.service);
    this.datasource.loadData();
    //console.log(moment().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
    //this.angForm.get('start').setValue(moment().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
    //this.angForm.get('end').setValue(moment().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
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
      .downloadIncidentEscalationReport(this.searchedData)
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
      let status = this.angForm.controls['status'].value;
      let priorityId = this.angForm.controls['priorityId'].value;
      let subModuleId = this.angForm.controls['subModuleId'].value;
      let moduleId = this.angForm.controls['moduleId'].value;
      let start = null;
      let end = null;
      let mnt = null;
      if (this.angForm.controls['start'].value != "") {
        mnt = moment(this.angForm.controls['start'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
        start = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T00:00:00";
      } else {
        start = moment().utc().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T00:00:00";
      }
      if (this.angForm.controls['end'].value != "") {
        mnt = moment(this.angForm.controls['end'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
        end = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T23:59:59";
      } else {
        end = moment().utc().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T23:59:59";
      }
      this.searchedData = {
        "status": status == null ? '' : status,
        "priorityId": priorityId == null ? '' : priorityId,
        "subModuleId": subModuleId == null ? '' : subModuleId,
        "moduleId": moduleId == null ? '' : moduleId,
        "start": start,
        "end": end,
      };
      //console.log(this.searchedData);
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
      let sm;
      for (let i = 0; i < this.allModuleList.length; i++) {
        if (this.allModuleList[i].appModule == false && this.allModuleList[i].parentModuleId == moduleId.id) {
          if (this.loggedInUserService.isAgentLead() || this.loggedInUserService.isOrgAdmin() || this.loggedInUserService.isAgentManager()) {
            sm = this.allModuleList[i];
            sm.customerModuleName = AppUtility.toTitleCase(sm.customerModuleName);
            this.subModuleList[c] = sm;
            c++;
          } else if (this.loggedInUserService.hasPermissionById(this.allModuleList[i].id, 'READ') || this.loggedInUserService.hasPermissionById(this.allModuleList[i].id, 'SOLVE')) {
            sm = this.allModuleList[i];
            sm.customerModuleName = AppUtility.toTitleCase(sm.customerModuleName);
            this.subModuleList[c] = sm;
            c++;
          }
        }
      }
    }
  }

}