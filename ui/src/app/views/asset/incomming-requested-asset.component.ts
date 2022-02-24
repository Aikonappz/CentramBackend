import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { take, tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { Asset } from '../../model/Asset';
import { AssetRequest } from '../../model/AssetRequest';
import { AssetType } from '../../model/enumerator/AssetType';
import { ProductCategory } from '../../model/enumerator/ProductCategory';
import { IAllocateAsset } from '../../model/IAllocateAsset';
import { AssetRequestService } from '../../service/AssetRequestService';
import { AssetService } from '../../service/AssetService';
import { RequestedAssetDataSource } from '../../service/datasource/RequestedAssetDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { AllocateAsset } from './model/AllocateAsset';
declare var $: any;

@Component({
  selector: 'app-incomming-requested-asset',
  templateUrl: './incomming-requested-asset.component.html',
  styleUrls: ['./incomming-requested-asset.component.scss']
})
export class IncommingRequestedAssetComponent implements OnInit {
  moduleName: string = "REQUESTED ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['reqDtl', 'mngRes', 'usrDtl', 'action'];
  private datasource: RequestedAssetDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  searchedData: Object = {};
  assetList: Set<string> = new Set<string>();
  modelList: Set<string> = new Set<string>();
  productTypes: Set<string> = new Set<string>();
  assetModelList: any[] = [];
  asset: Asset[] = [];
  modalRef: BsModalRef;

  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: AssetRequestService,
    private assetService: AssetService,
    private miscService: MiscService,
    private modalService: BsModalService,
    private loggedInUserService: LoggedInUserService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.miscService
      .assetModelsService()
      .subscribe((data: any) => {
        this.assetModelList = data;
        for (let k in data) {
          if (data[k].status == "ACTIVE")
            this.productTypes.add(data[k].productCategory);
        }
      });
    this.angForm = this.fb.group({
      productCategory: new FormControl('', [
      ]),
      assetType: new FormControl('', [
      ]),
      modelNo: new FormControl(null, [
      ]),
      serialNumber: new FormControl(null, [
      ]),
      allocated: new FormControl(null, [
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
    this.datasource = new RequestedAssetDataSource(this.service);
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

  allocate(assetRequest: AssetRequest, allocate: boolean) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState: Partial<IAllocateAsset> = {
      assetRequest: assetRequest,
      allocate: allocate
    };
    this.modalRef = this.modalService.show(AllocateAsset, Object.assign({}, config, { initialState }));
    this.modalService.onHide
      .pipe(take(1))
      .subscribe(() => {
        this.loadData(this.searchedData);
      });
  }

  loadData(req?: Object) {
    //console.log(req);
    if (this.searchedData.hasOwnProperty('productCategory')) {
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
      let productCategory = this.angForm.controls['productCategory'].value;
      let assetType = this.angForm.controls['assetType'].value;
      let modelNo = this.angForm.controls['modelNo'].value;
      let serialNumber = this.angForm.controls['serialNumber'].value;
      let allocated = this.angForm.controls['allocated'].value;
      this.searchedData = {
        "productCategory": productCategory == "" || productCategory == null ? -1 : ProductCategory[productCategory],
        "assetType": assetType == "" || assetType == null ? -1 : AssetType[assetType],
        "modelNo": modelNo == "" || modelNo == null ? '' : modelNo,
        "serialNo": serialNumber == "" || serialNumber == null ? '' : serialNumber,
        "allocated": allocated == "" || allocated == null ? -1 : allocated,
      };
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  get f() { return this.angForm.controls; }

  @ViewChild("productCategory") productCategory;
  populateChildValues(productCategory: string) {
    if (productCategory != "") {
      this.assetList = new Set<string>();
      this.modelList = new Set<string>();
      for (let k in this.assetModelList) {
        if (this.assetModelList[k].productCategory == productCategory && this.assetModelList[k].status == "ACTIVE") {
          this.assetList.add(this.assetModelList[k].assetType);
          this.modelList.add(this.assetModelList[k].modelNo);
        }
      }
      this.angForm.controls['assetType'].setValue("");
      this.angForm.controls['modelNo'].setValue("");
    }
  }

  @ViewChild("assetType") assetType;
  populateAssetModels(assetType: string) {
    if (assetType != "") {
      this.modelList = new Set<string>();
      for (let k in this.assetModelList) {
        if (this.assetModelList[k].assetType == assetType && this.assetModelList[k].status == "ACTIVE") {
          this.modelList.add(this.assetModelList[k].modelNo);
        }
      }
      this.angForm.controls['modelNo'].setValue("");
    }
  }
}