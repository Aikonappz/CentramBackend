import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MediaService } from '../../service/MediaService';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { ClientStorageService } from '../../service/ClientStorageService';
import { AssetRequestService } from '../../service/AssetRequestService';
import { AssetRequest } from '../../model/AssetRequest';
import { EntityType } from '../../model/enumerator/EntityType';
import { MediaType } from '../../model/enumerator/MediaType';
declare var $: any;

@Component({
  selector: 'app-request-asset',
  templateUrl: './request-asset.component.html',
  styleUrls: ['./request-asset.component.scss']
})
export class RequestAssetComponent implements OnInit {
  moduleName: string = "REQUEST ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'OPEN';
  statusFlag: boolean = true;
  entityId: number;
  angForm: FormGroup;
  departmentList: any[] = [];
  locationList: any[] = [];
  vendorList: any[] = [];
  purchaseTypeList: any[] = [];
  assetRequest: AssetRequest;
  assetList: Set<string> = new Set<string>();
  modelList: Set<string> = new Set<string>();
  assetModelList: any[] = [];
  productTypes: Set<string> = new Set<string>();
  selectedFiles?: FileList;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
    private assetRequestService: AssetRequestService,
    private miscService: MiscService,
    private userService: UserService,
    private mediaService: MediaService,
    private clientStorageService: ClientStorageService,
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
        Validators.required,
      ]),
      assetType: new FormControl('', [
        Validators.required,
      ]),
      modelNo: new FormControl('', [
        //Validators.required,
      ]),
      longTerm: new FormControl('', [
        Validators.required,
      ]),
      comment: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
      ]),
      fileInput: new FormControl(null, [
      ]),
    }, {
    });
    this.assetRequest = new AssetRequest();
  }

  hasPermission(actions: string): boolean {
    let modules = this.moduleName.split(",");
    let actionList = actions.split(",");
    for (let i in modules) {
      if (this.loggedInUserService.hasPermissionByName(modules[i], actionList[i])) {
        return true;
      }
    }
    return false;
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
      //console.log(this.route.snapshot.paramMap.get('referer'));
    });
    if (!this.route.snapshot.paramMap.has('id')) {
    } else {
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
    }
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

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

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      this.assetRequest.productCategory = this.angForm.controls['productCategory'].value;
      this.assetRequest.assetType = this.angForm.controls['assetType'].value;
      this.assetRequest.modelNo = this.angForm.controls['modelNo'].value == "" ? null : this.angForm.controls['modelNo'].value;
      this.assetRequest.longTerm = this.angForm.controls['longTerm'].value == 1 ? true : false;
      this.assetRequest.comment = this.angForm.controls['comment'].value;
      //console.log(JSON.stringify(this.asset));
      this.callSaveAssetService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this._location.back();
  }

  callSaveAssetService() {
    this.assetRequestService
      .saveAssetRequest(this.assetRequest)
      .subscribe((data: any) => {
        if (typeof this.selectedFiles != "undefined") {
          if (this.selectedFiles.length > 0) {
            const formData: FormData = new FormData();
            for (var i = 0; i < this.selectedFiles.length; i++) {
              formData.append("file", this.selectedFiles[i]);
            }
            let headers = new Headers();
            headers.append('Content-Type', 'multipart/form-data');
            headers.set('Accept', 'application/json');
            let commId = data.id;
            this.mediaService
              .saveMediaService(commId, EntityType.ASSET_REQUEST, MediaType.ASSET_REQUEST, formData, { 'headers': headers })
              .subscribe((data: any) => {
                this.router.navigate(['/asset/requested']);
              });
          } else {
            this.router.navigate(['/asset/requested']);
          }
        } else {
          this.router.navigate(['/asset/requested']);
        }
      });
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  getFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angForm.controls['fileInput'];
      if (file.errors && !file.errors.validAttachments && !file.errors.mustBeLessThan2MB) {
        return;
      }
      let validMimeTpes = ["application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "application/x-msexcel", "application/x-excel", "application/vnd.ms-excel", "application/excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingm", "image/jpeg", "image/pjpeg", "image/png"];
      if (!validMimeTpes.includes(type)) {
        file.setErrors({ validAttachments: true, mustBeLessThan2MB: false });
      } else if (size > (3145728)) {
        file.setErrors({ validAttachments: false, mustBeLessThan2MB: true });
      } else {
        file.setErrors(null);
        this.selectedFiles = event.target.files;
      }
      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");
    }
  }

}