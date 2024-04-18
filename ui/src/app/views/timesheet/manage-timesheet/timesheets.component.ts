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
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ManageTimeSheetComponent } from '../../project/manage-timesheet.component';
import { ManageTimeSheetInputVO } from '../../../model/ManageTimeSheetInputVO';
import { Project } from '../../../model/Project';

@Component({
  selector: 'app-timesheets',
  templateUrl: './timesheets.component.html',
  styleUrls: ['./timesheets.component.scss']
})
export class TimeSheetsComponent implements OnInit {
  moduleName: string = "TIMESHEET SUBMIT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['fromDate', 'toDate', 'action'];
  datasource: TimeSheetDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  hasAllocationType: boolean = true;
  angForm: FormGroup;
  searchedData: any = {};
  manageTimeSheetInputVO: ManageTimeSheetInputVO;
  projects: Project[] = [];
  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private service: MiscService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.service.getUserProjects()
      .subscribe((result: ManageTimeSheetInputVO) => {
        this.manageTimeSheetInputVO = result;
        this.projects = this.manageTimeSheetInputVO.projects;
        //this.billingTypes = this.manageTimeSheetInputVO.billingTypes;
        //this.tasks = this.manageTimeSheetInputVO.tasks;
        //this.locationTypes = this.manageTimeSheetInputVO.locations;
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
    this.route.params.subscribe(params => {
      this.angForm = this.fb.group({
        date: new FormControl(null, [
        ]),
        projectId: new FormControl(null, []),
      }, {
      });
      this.hasAllocationType = true;
      this.datasource = new TimeSheetDataSource(this.service);
      this.datasource.loadData(0, 10, {});

    });
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      let date = null;
      let projectId = null;
      let mnt = null;
      if (this.angForm.controls['date'].value != "" && this.angForm.controls['date'].value != null && this.angForm.controls['date'].value !== 'undefined') {
        date = moment(this.angForm.controls['date'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
        this.searchedData.date = date;
      }
      if (this.angForm.controls['projectId'].value != "" && this.angForm.controls['projectId'].value != null && this.angForm.controls['projectId'].value !== 'undefined') {
        projectId = this.angForm.controls['projectId'].value;
        this.searchedData.projectId = projectId;
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
    this.router.navigate(['/timesheet/timesheet/edit/' + timeSheet.id]);
  }

  add() {
    this.router.navigate(['/timesheet/timesheet/add']);
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
