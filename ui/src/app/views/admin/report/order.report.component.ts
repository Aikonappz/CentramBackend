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

@Component({
  selector: 'app-order-report',
  templateUrl: './order.report.component.html',
  styleUrls: ['./order.report.component.scss']
})
export class OrderReportComponent implements OnInit {
  moduleName: string = "ORDER REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['orderFor', 'assetDtl', 'approverDtl',];
  datasource: ReportAssetOrderDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: any[] = [];
  angForm: FormGroup;
  searchedData: any = {};
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
    this.statusList.push({ id: 'PENDING', label: 'Pending Approval' });
    this.statusList.push({ id: 'PARTIALLY_APPROVED', label: '1st Level Approved' });
    this.statusList.push({ id: 'APPROVED', label: '2nd Level Approved' });
    this.angForm = this.fb.group({
      orderNo: new FormControl('', [
      ]),
      status: new FormControl(null, [
      ]),
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
    this.datasource = new ReportAssetOrderDataSource(this.service);
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
      .downloadOrderReport(this.searchedData)
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
    this.searchedData = {};
    this.loadData({});
  }

  formSubmit() {
    if (this.angForm.valid) {
      let status = this.angForm.controls['status'].value;
      let orderNo = this.angForm.controls['orderNo'].value;
      this.searchedData = {
        "status": status == null ? '' : status,
        "orderNo": orderNo == null ? '' : orderNo,
      };
      //console.log(JSON.stringify(this.searchedData));
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

}