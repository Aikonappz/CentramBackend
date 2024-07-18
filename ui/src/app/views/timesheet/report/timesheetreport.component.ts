import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../../config/AppUtility';
import { ReportAssetOrderDataSource } from '../../../service/datasource/ReportAssetOrderDataSource';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { MiscService } from '../../../service/MiscService';
import { ReportService } from '../../../service/ReportService';
import { UserService } from '../../../service/UserService';
import { ProjectList } from '../../../model/Project';
import { Technology } from '../../../model/enumerator/Technology';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { ProjectUat } from '../../../model/ProjectUat';
import { ProjectUatScript } from '../../../model/ProjectUatScript';
import { ProjectUatService } from '../../../service/ProjectUatService';
import { UatScriptReportDataSource } from '../../../service/datasource/UatScriptReportDataSource';
import { LicenseType } from '../../../model/enumerator/LicenseType';
import { StartEndDateValidation } from '../../../validator/StartEndDateValidation';
import { TimesheetReportDataSource } from '../../../service/datasource/TimesheetReportDataSource';

@Component({
  selector: 'app-timesheet-report',
  templateUrl: './timesheetreport.component.html',
  styleUrls: ['./timesheetreport.component.scss']
})
export class TimesheetReportComponent implements OnInit {
  moduleName: string = "UAT REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  //displayedColumns = ['projDtl', 'consltDtl', 'testCaseDtl', 'status',];
  displayedColumns = ['user', 'project', 'work', 'approver',];
  datasource: TimesheetReportDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  timesheetSearchForm: FormGroup;
  searchedData: any = { userIds: [], includeAll: true };
  projectList: any[];
  loggedInUser: LoggedInUser;
  userList: any[] = [];
  statusList: any[] = [];

  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: ReportService,
    private userService: UserService,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private route: ActivatedRoute,
    private projectUatService: ProjectUatService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.miscService.projectsService({ projectFor: "TIMESHEET" })
      .subscribe((result: ProjectList) => {
        this.projectList = [];
        for (let k = 0; k < result.content.length; k++) {
          this.projectList[k] = result.content[k];
          this.projectList[k].label = this.projectList[k].name + " [" + this.projectList[k].code + "]";
        }
      });
    this.statusList.push({ id: 'PENDING', label: 'Pending' });
    this.statusList.push({ id: 'APPROVED', label: 'Approved' });
    this.timesheetSearchForm = this.fb.group({
      searchProject: new FormControl(null, [
      ]),
      searchUsers: new FormControl(null, [
      ]),
      status: new FormControl(null, [
      ]),
      start: new FormControl(moment().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
      ]),
      end: new FormControl(moment().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
      ]),
    }, {
      validators: StartEndDateValidation('start', 'end')
    })
  }

  /**
 * 
 */
  @ViewChild("searchProject") searchProject;
  populateUsers(searchProject) {
    if (typeof searchProject !== 'undefined') {
      this.miscService
        .allocatedUsersService({
          projectId: searchProject.id,
          includeDeallocated: true
        })
        .subscribe((data: any) => {
          this.userList = [];
          for (let k = 0; k < data.content.length; k++) {
            //console.log(data.content[k]);
            this.userList[k] = data.content[k];
            this.userList[k].label = this.userList[k].firstName + " " + this.userList[k].lastName + " [" + this.userList[k].email + "]";
          }
        });
    } else {
      this.userList = [];
      this.timesheetSearchForm.get('searchUsers').setValue(null);
    }
  }


  /**
   * 
   * @param roles 
   * @returns 
   */
  hasRoles(roles: string[]) {
    let userRoles = this.loggedInUserService.getRoles();
    for (let k = 0; k < userRoles.length; k++) {
      if (roles.includes(userRoles[k])) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @param action 
   * @returns 
   */
  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
  }

  /**
   * 
   * @param state 
   * @param parent 
   * @returns 
   */
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
    this.datasource = new TimesheetReportDataSource(this.service);
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

  get f() { return this.timesheetSearchForm.controls; }

  download() {
    this.service
      .downloadUatScriptReport(this.searchedData)
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
    this.timesheetSearchForm.reset();
    this.searchedData = { userIds: [], includeAll: true };;
    this.loadData({});
  }

  formSubmit() {
    if (this.timesheetSearchForm.valid) {
      let projectId = this.timesheetSearchForm.controls['searchProject'].value;
      let userIds = this.timesheetSearchForm.controls['searchUsers'].value;
      let status = this.timesheetSearchForm.controls['status'].value != null ? this.timesheetSearchForm.controls['status'].value == 'PENDING' ? false : true : null;
      let start = null;
      let end = null;
      let mnt = null;
      if (this.timesheetSearchForm.controls['start'].value != "" && this.timesheetSearchForm.controls['end'].value != "") {
        mnt = moment(this.timesheetSearchForm.controls['start'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
        start = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T00:00:00";
        mnt = moment(this.timesheetSearchForm.controls['end'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
        end = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T23:59:59";
      }
      this.searchedData = {
        "projectId": projectId == null ? '' : projectId,
        "userIds": userIds == null ? '' : userIds,
        "start": start,
        "end": end,
        "includeAll": status != null ? false : true,
        "approved": status != null ? status : false,
      };
      console.log(JSON.stringify(this.searchedData));
      this.loadData();
    } else {
      console.log("Invalid Form!");
    }
  }

  format(str: String) {
    // var strArray = 
    // strArray = strArray.filter((item) => {
    //   return item !== '';
    // });
    //console.log(strArray.join(",").split(",,").join(","));
    return str.replaceAll(',', '<br/>');
  }

}