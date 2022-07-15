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
import { Incident } from '../../model/Incident';
import { LoggedInUser } from '../../model/LoggedInUser';
import { PendingAssetApprovalDataSource } from '../../service/datasource/PendingAssetApprovalDataSource';
import { IncidentService } from '../../service/IncidentService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { ViewAssetOrderDetail } from './modal/ViewAssetOrderDetail';
import { ViewAssetRequestDetail } from './modal/ViewAssetRequestDetail';
declare var $: any;

@Component({
  selector: 'pending-asset-approval.component',
  templateUrl: './pending-asset-approval.component.html',
  styleUrls: ['./pending-asset-approval.component.scss']
})
export class PendingAssetApprovalComponent implements OnInit {
  moduleName: string = "MY ASSET";
  displayedColumns = ['orderFor', 'assetDtl', 'action'];
  private datasource: PendingAssetApprovalDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  searchedData: Object = {};
  modalRef: BsModalRef;
  loggedInUser: LoggedInUser;

  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: IncidentService,
    private loggedInUserService: LoggedInUserService,
    private modalService: BsModalService,
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
    this.datasource = new PendingAssetApprovalDataSource(this.service);
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
      this.searchedData = {
        "incidentNo": this.angForm.controls['orderNo'].value == null ? '' : this.angForm.controls['orderNo'].value,
      };
      //console.log(JSON.stringify(this.searchedData));
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  view(element: Incident) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      incident: element
    };
    this.modalRef = this.modalService.show(ViewAssetRequestDetail, Object.assign({}, config, { initialState }));
  }

  takeAction(element: AssetOrder) {

    if ((!element.approvedUser1 && element.approverUser1Comment == null) && element.approverUser1.id == this.loggedInUser.userId) {
      //1
    }

    if (element.approvedUser1 && (!element.approvedUser2 && element.approverUser2Comment == null) && element.approverUser1.id == this.loggedInUser.userId) {

    }

    if (element.approvedUser1 && element.approvedUser2 && element.approverUser1.id == this.loggedInUser.userId) {

    } else if (element.approvedUser1 && element.approvedUser2 && element.approverUser2.id == this.loggedInUser.userId) {

    } else if (element.approvedUser1 && element.approvedUser2 && element.approverUser2.id == this.loggedInUser.userId) {

    }

  }

}