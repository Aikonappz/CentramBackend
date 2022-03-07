import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { Asset } from '../../model/Asset';
import { AssetType } from '../../model/enumerator/AssetType';
import { ProductCategory } from '../../model/enumerator/ProductCategory';
import { Incident } from '../../model/Incident';
import { AssetService } from '../../service/AssetService';
import { AssetDataSource } from '../../service/datasource/AssetDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { ViewAssetDetail } from './model/ViewAssetDetail';
declare var $: any;

@Component({
  selector: 'app-asset-inventory',
  templateUrl: './asset-inventory.component.html',
  styleUrls: ['./asset-inventory.component.scss']
})
export class AssetInventoryComponent implements OnInit {
  moduleName: string = "MANAGE ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['serialNo', 'productType', 'assetType', 'available', 'action'];
  private datasource: AssetDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  searchedData: Object = {};
  assetList: any[] = [];
  //modelList: Set<string> = new Set<string>();
  assetModelList: any[] = [];
  productTypes: any[] = [];
  booleanList: any[] = [];
  modalRef: BsModalRef;

  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: AssetService,
    private miscService: MiscService,
    private loggedInUserService: LoggedInUserService,
    private modalService: BsModalService,
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
        this.productTypes = [];
        let productCategories = new Set<string>();
        if (this.assetModelList.length > 0) {
          for (let k in data) {
            if (data[k].status == "ACTIVE")
              productCategories.add(data[k].productCategory);
          }
          for (var i = productCategories.values(), val = null; val = i.next().value;) {
            this.productTypes.push({ id: val, label: val });
          }
        }
      });
    this.booleanList.push({ id: 0, label: 'No' });
    this.booleanList.push({ id: 1, label: 'Yes' });
    this.angForm = this.fb.group({
      productCategory: new FormControl(null, [
      ]),
      assetType: new FormControl(null, [
      ]),
      modelNo: new FormControl('', [
      ]),
      serialNumber: new FormControl('', [
      ]),
      available: new FormControl(null, [
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
    this.datasource = new AssetDataSource(this.service);
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
    this.router.navigate(['/asset/inventory/edit/' + inc.id]);
  }

  add(mode: string) {
    this.router.navigate(['/asset/inventory/add/']);
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
      let available = this.angForm.controls['available'].value;
      //console.log(productCategory);
      this.searchedData = {
        "productCategory": productCategory == "" || productCategory == null ? -1 : ProductCategory[productCategory],
        "assetType": assetType == "" || assetType == null ? -1 : AssetType[assetType],
        "modelNo": modelNo == "" || modelNo == null ? '' : modelNo,
        "serialNo": serialNumber == "" || serialNumber == null ? '' : serialNumber,
        "available": available == "" || available == null ? -1 : available,
      };
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  get f() { return this.angForm.controls; }

  @ViewChild("productCategory") productCategory;
  populateChildValues(productCategory) {
    if (typeof productCategory !== 'undefined') {
      this.assetList = [];
      let assetTypes = new Set<string>();
      for (let k in this.assetModelList) {
        if (this.assetModelList[k].productCategory == productCategory.id && this.assetModelList[k].status == "ACTIVE") {
          assetTypes.add(this.assetModelList[k].assetType);
        }
      }
      for (var i = assetTypes.values(), val = null; val = i.next().value;) {
        this.assetList.push({ id: val, label: val });
      }
    }
  }

  download() {
    this.service
      .downloadAssetsService(this.searchedData)
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

  view(asset: Asset) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      element: asset
    };
    this.modalRef = this.modalService.show(ViewAssetDetail, Object.assign({}, config, { initialState }));
  }

}