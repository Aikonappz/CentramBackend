import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../../config/AppUtility';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { UserVO, UserVOListResponse } from '../../../model/UserVO';
import { AssetIncidentReportDataSource } from '../../../service/datasource/AssetIncidentReportDataSource';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { MiscService } from '../../../service/MiscService';
import { ReportService } from '../../../service/ReportService';
import { UserService } from '../../../service/UserService';

@Component({
  selector: 'app-asset-assignment-report',
  templateUrl: './asset.asignment.report.component.html',
  styleUrls: ['./asset.asignment.report.component.scss']
})
export class AssetAssignmentReportComponent implements OnInit {
  moduleName: string = "ASSET ASSIGNMENT REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['user', 'assetDtl', 'validity', 'allocation', 'deallocation', 'status',];
  datasource: AssetIncidentReportDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  userList: UserVO[] = [];
  loggedInUser: LoggedInUser;
  searchedData: any = { incidentType: "ASSET", assigned: 1, };
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
    this.booleanList.push({ id: 1, label: 'Deallocated' });
    this.booleanList.push({ id: 0, label: 'Allocated' });
    this.angForm = this.fb.group({
      empId: new FormControl(null, [
      ]),
      alocationStatus: new FormControl(null, [
      ]),
    });
    this.userService.getUsersService({})
      .subscribe((data: UserVOListResponse) => {
        this.userList = [];
        for (let i in data.content) {
          data.content[i].label = data.content[i].employeeId + " [" + data.content[i].fullName + "]";
          this.userList.push(data.content[i]);
        }
        //console.log(this.userList);
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
      .downloadAssetAssignmentReport(this.searchedData)
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

  formatDate(d: string) {
    if (d != null && d != "") {
      //return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
      return moment.utc(d).format(AppUtility.APP_VIEW_DATE_FORMAT);
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
      let empId = this.angForm.controls['empId'].value;
      let alocationStatus = this.angForm.controls['alocationStatus'].value;
      this.searchedData.raisedUserId = empId == null ? '' : empId;
      if (alocationStatus == 0) {
        this.searchedData.deallocated = 0;
        this.searchedData.assigned = 1;
      } else if (alocationStatus == 1) {
        this.searchedData.assigned = 1;
        this.searchedData.deallocated = 1;
      } else {
        this.searchedData.assigned = 1;
        this.searchedData.deallocated = -1;
      }
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

}