import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { Incident } from '../../model/Incident';
import { Permission } from '../../model/Permssion';
import { AssetOrderService } from '../../service/AssetOrderService';
import { AssetOrderDataSource } from '../../service/datasource/AssetOrderDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
declare var $: any;

@Component({
  selector: 'app-orderasset',
  templateUrl: './orderasset.component.html',
  styleUrls: ['./orderasset.component.scss']
})
export class OrderAssetComponent implements OnInit {
  moduleName: string = "ORDER ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['orderFor', 'assetDtl', 'vendorDtl', 'approverDtl',];
  private datasource: AssetOrderDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: any = [];
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  angForm: FormGroup;
  searchedData: Object = {};
  incidentStatus: IncidentStatus;
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: AssetOrderService,
    private loggedInUserService: LoggedInUserService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      orderNo: new FormControl('', [
      ]),
      title: new FormControl('', [
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
    this.datasource = new AssetOrderDataSource(this.service);
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
  }

  edit(inc: Incident) {
    this.router.navigate(['/incident/user/edit/' + inc.id]);
  }

  add(mode: string) {
    this.router.navigate(['/asset/order/add/']);
  }

  loadData(req?: Object) {
    //console.log(req);
    if (this.searchedData.hasOwnProperty('orderNo')) {
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
      let status = this.angForm.controls['status'].value;
      let orderNo = this.angForm.controls['orderNo'].value;
      this.searchedData = {
        "status": status == null ? '' : status,
        "orderNo": orderNo == null ? '' : orderNo,
      };
      this.loadData(this.searchedData);
      //console.log(JSON.stringify(this.org));
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
    }
  }
}