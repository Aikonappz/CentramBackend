import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { AssetOrder } from '../../model/AssetOrder';
import { AssetOrderService } from '../../service/AssetOrderService';
import { AssetOrderDataSource } from '../../service/datasource/AssetOrderDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { ViewAssetOrderDetail } from './modal/ViewAssetOrderDetail';
declare var $: any;

@Component({
  selector: 'app-ordered-asset',
  templateUrl: './ordered-asset.component.html',
  styleUrls: ['./ordered-asset.component.scss']
})
export class OrderedAssetComponent implements OnInit {
  moduleName: string = "ORDER ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['orderFor', 'assetDtl', 'approverDtl', 'action'];
  private datasource: AssetOrderDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  searchedData: Object = {};
  modalRef: BsModalRef;
  statusList: any[] = [];

  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: AssetOrderService,
    private loggedInUserService: LoggedInUserService,
    private modalService: BsModalService,
  ) {
    this.statusList.push({ id: 'PENDING', label: 'Pending Approval' });
    this.statusList.push({ id: 'PARTIALLY_APPROVED', label: '1st Level Approved' });
    this.statusList.push({ id: 'APPROVED', label: '2nd Level Approved' });
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
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

  add(mode: string) {
    this.router.navigate(['/asset/order/']);
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
      //console.log(JSON.stringify(this.searchedData));
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  view(element: AssetOrder) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      assetOrder: element
    };
    this.modalRef = this.modalService.show(ViewAssetOrderDetail, Object.assign({}, config, { initialState }));
  }

}