import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { tap } from 'rxjs/operators';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { MiscService } from '../../../service/MiscService';
import { AccountDataSource } from '../../../service/datasource/AccountDataSource';
import { Account } from '../../../model/Account';
import { TimeSheetDataSource } from '../../../service/datasource/TimeSheetDataSource';
import * as moment from 'moment';
import { AppUtility } from '../../../config/AppUtility';
import { TimeSheet } from '../../../model/TimeSheet';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { TimeSheetApprovalDataSource } from '../../../service/datasource/TimeSheetActionDataSource';
import { ProjectList } from '../../../model/Project';
import { UserService } from '../../../service/UserService';
import { UserVOListResponse } from '../../../model/UserVO';
import { LoggedInUser } from '../../../model/LoggedInUser';

@Component({
  selector: 'app-timesheet-approval-list',
  templateUrl: './timesheet-approval-list.component.html',
  styleUrls: ['./timesheet-approval-list.component.scss']
})
export class TimeSheetApprovalListComponent implements OnInit {
  moduleName: string = "TIMESHEET APPROVAL";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['fromDate', 'toDate', 'user', 'action'];
  //displayedColumns = ['fromDate', 'toDate', 'action'];
  datasource: TimeSheetApprovalDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  searchedData: any = {};
  types: any[] = [];
  projects: any[] = [];
  //projectList_: any[] = [];
  userList: any[] = [];
  loggedInUser: LoggedInUser;
  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private service: MiscService,
    private userService: UserService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
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
    this.route.params.subscribe(params => {
      this.angForm = this.fb.group({
        date: new FormControl(null, [
        ]),
        projectId: new FormControl(null, [
        ]),
        userId: new FormControl(null, [
        ]),
        pendingApproval: new FormControl(null, [
        ]),
      }, {
      });
      this.datasource = new TimeSheetApprovalDataSource(this.service);
      this.datasource.loadData(0, 10, {});
      this.types.push({ id: -1, label: "All" });
      this.types.push({ id: 1, label: "Pending Only" });
      this.userService.getUsersService()
        .subscribe((result: UserVOListResponse) => {
          if (result.content != null && result.content.length > 0) {
            this.userList = result.content;
            for (let k = 0; k < this.userList.length; k++) {
              this.userList[k].label = this.userList[k].fullName + (this.userList[k].employeeId != null ? "[" + this.userList[k].employeeId + "]" : "");
            }
            //console.log(this.userList);
          }
        });
      this.service.projectsService({ projectFor: 'TIMESHEET' })
        .subscribe((data: ProjectList) => {
          if (data.content != null && data.content.length > 0) {
            let c = 0;
            for (let k = 0; k < data.content.length; k++) {
              //if (data.content[k].approvers.includes(this.loggedInUser.email)) {
              //console.log(data.content[k].approvers)
              //this.projects[c] = data.content[k];
              //this.projects.push(data.content[k]);
              //c++;
              //}
            }
            // //this.projectList_ = this.projectList;
            // console.log(this.projects);
            this.projects = data.content;
          }
        });

    });
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.searchedData = {};
      if (this.angForm.controls['date'].value != "" && this.angForm.controls['date'].value != null) {
        this.searchedData.date = moment(this.angForm.controls['date'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
      }
      if (this.angForm.controls['projectId'].value != "" && this.angForm.controls['projectId'].value != null) {
        this.searchedData.projectId = this.angForm.controls['projectId'].value;
      }
      if (this.angForm.controls['userId'].value != "" && this.angForm.controls['userId'].value != null) {
        this.searchedData.userId = this.angForm.controls['userId'].value;
      }
      if (this.angForm.controls['pendingApproval'].value != "" && this.angForm.controls['pendingApproval'].value != null) {
        this.searchedData.pendingApproval = this.angForm.controls['pendingApproval'].value;
      }
      //console.log(this.searchedData);
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  get f() { return this.angForm.controls; }

  loadPage() {
    this.angForm.reset();
    this.searchedData = {};
    this.loadData({});
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
        tap(() => this.loadData({}))
      )
      .subscribe();
  }

  edit(timeSheet: TimeSheet) {
    //console.log(JSON.stringify(timeSheet));
    this.router.navigate(['/timesheet/operation/timesheet-approval/' + timeSheet.id]);
  }

  add() {
    this.router.navigate(['/timesheet/add']);
  }

  loadData(req: any = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }
}
