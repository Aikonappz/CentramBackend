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
import { LoggedInUser } from '../../model/LoggedInUser';
import { AssetOrderService } from '../../service/AssetOrderService';
import { AssetOrderActionDataSource } from '../../service/datasource/AssetOrderActionDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { ViewAssetOrderDetail } from './modal/ViewAssetOrderDetail';
declare var $: any;

@Component({
  selector: 'app-in-ordered-asset',
  templateUrl: './in-ordered-asset.component.html',
  styleUrls: ['./in-ordered-asset.component.scss']
})
export class OrderedAssetActionComponent implements OnInit {
  moduleName: string = "ORDERED ASSET ACTION";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['orderFor', 'assetDtl', 'approverDtl', 'action'];
  private datasource: AssetOrderActionDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  searchedData: Object = {};
  modalRef: BsModalRef;
  statusList: any[] = [];
  loggedInUser: LoggedInUser;

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
    this.datasource = new AssetOrderActionDataSource(this.service);
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

  takeAction(element: AssetOrder){

   if((!element.approvedUser1 && element.approverUser1Comment == null) && element.approverUser1.id == this.loggedInUser.userId){
      //1
   }
   
   if(element.approvedUser1 && (!element.approvedUser2 && element.approverUser2Comment == null) && element.approverUser1.id == this.loggedInUser.userId){
     
   }
    
    if(element.approvedUser1 && element.approvedUser2 && element.approverUser1.id == this.loggedInUser.userId){

    }else if(element.approvedUser1 && element.approvedUser2 && element.approverUser2.id == this.loggedInUser.userId){

    }else if(element.approvedUser1 && element.approvedUser2 && element.approverUser2.id == this.loggedInUser.userId){

    }

  }

}