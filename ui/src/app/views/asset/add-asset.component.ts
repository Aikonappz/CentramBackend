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
import { PurchaseType } from '../../model/enumerator/PurchaseType';
import { Asset } from '../../model/Asset';
import { AssetService } from '../../service/AssetService';
declare var $: any;

@Component({
  selector: 'app-add-asset',
  templateUrl: './add-asset.component.html',
  styleUrls: ['./add-asset.component.scss']
})
export class AddAssetComponent implements OnInit {
  moduleName: string = "MANAGE ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  newEntity: boolean = true;
  entityId: number;
  angForm: FormGroup;
  departmentList: any[] = [];
  locationList: any[] = [];
  vendorList: any[] = [];
  purchaseTypeList: any[] = [];
  asset: Asset;
  assetList: any[] = [];
  assetModelList: any[] = [];
  productTypes: any[] = [];
  booleanList: any[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
    private assetService: AssetService,
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
    let purchaseTypeList = Object.values(PurchaseType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));
    for (let k in purchaseTypeList) {
      this.purchaseTypeList.push({ id: purchaseTypeList[k], label: purchaseTypeList[k] });
    }
    this.angForm = this.fb.group({
      productCategory: new FormControl(null, [
        Validators.required,
      ]),
      assetType: new FormControl(null, [
        Validators.required,
      ]),
      modelNo: new FormControl('', [
        Validators.required,
      ]),
      serialNumber: new FormControl('', [
        //Validators.required,
        Validators.maxLength(255),
      ]),
      isDepartment: new FormControl(null, [
        Validators.required,
      ]),
      department: new FormControl(null, [
      ]),
      location: new FormControl(null, [
      ]),
      forLocation: new FormControl(null, [
        Validators.required,
      ]),
      raisedForLocation: new FormControl(null, [
      ]),
      isUnderWarranty: new FormControl(null, [
        Validators.required,
      ]),
      warrantyExpiredAt: new FormControl('', [
        Validators.required,
      ]),
      purchaseType: new FormControl(null, [
        Validators.required,
      ]),
      rentalStartAt: new FormControl('', [
      ]),
      rentalEndAt: new FormControl('', [
      ]),
      vendor: new FormControl(null, [
        Validators.required,
      ]),
      vendorName: new FormControl('', [
      ]),
      vendorEmail: new FormControl('', [
      ]),
      vendorContactNo: new FormControl('', [
      ]),
    }, {
      validators: this.customValidations(),
    });
    this.asset = new Asset();
  }

  customValidations() {
    return (formGroup: FormGroup) => {
      //serial no validator
      for (let k in this.assetModelList) {
        if (
          this.assetModelList[k].productCategory == formGroup.controls['productCategory'].value &&
          this.assetModelList[k].assetType == formGroup.controls['assetType'].value
        ) {
          if (this.assetModelList[k].generateAssetNo == false && formGroup.controls['serialNumber'].value == "") {
            formGroup.controls['serialNumber'].setErrors({ required: true });
            break;
          } else if (this.assetModelList[k].generateAssetNo == false && formGroup.controls['serialNumber'].value != "") {
            formGroup.controls['serialNumber'].setErrors(null);
          } else {
            formGroup.controls['serialNumber'].setErrors(null);
          }
        } else {
          continue;
        }
      }
      //department/office location validator
      if (formGroup.controls['isDepartment'].value == "1") {
        $('#dept-inp').removeClass("d-none");
        $('#ofc-inp').addClass("d-none");
        formGroup.controls['location'].setErrors(null);
        if (formGroup.controls['department'].value == null) {
          formGroup.controls['department'].setErrors({ required: true });
        } else {
          formGroup.controls['department'].setErrors(null);
        }
      } else {
        $('#dept-inp').addClass("d-none");
        $('#ofc-inp').removeClass("d-none");
        formGroup.controls['department'].setErrors(null);
        if (formGroup.controls['location'].value == null) {
          formGroup.controls['location'].setErrors({ required: true })
        } else {
          formGroup.controls['location'].setErrors(null);
        }
      }
      //raisedForLocation location validator
      if (formGroup.controls['forLocation'].value == "1") {
        $('#loc-inp').removeClass("d-none");
        $('#proxy-lic-inp').addClass("d-none");
        if (formGroup.controls['raisedForLocation'].value == null) {
          formGroup.controls['raisedForLocation'].setErrors({ required: true });
        } else {
          formGroup.controls['raisedForLocation'].setErrors(null);
        }
      } else {
        $('#loc-inp').addClass("d-none");
        $('#proxy-lic-inp').removeClass("d-none");
        formGroup.controls['raisedForLocation'].setErrors(null);
      }
      //warranty validator
      if (formGroup.controls['isUnderWarranty'].value == "1") {
        if (formGroup.controls['warrantyExpiredAt'].value == null || formGroup.controls['warrantyExpiredAt'].value == "") {
          formGroup.controls['warrantyExpiredAt'].setErrors({ required: true, mustGreaterThanStartDate: false });
        } else {
          let warrantyD = moment((formGroup.controls['warrantyExpiredAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
          if (warrantyD <= new Date()) {
            formGroup.controls['warrantyExpiredAt'].setErrors({ required: false, mustGreaterThanStartDate: true });
          } else {
            formGroup.controls['warrantyExpiredAt'].setErrors(null);
          }
        }
      } else {
        if (formGroup.controls['warrantyExpiredAt'].value == null || formGroup.controls['warrantyExpiredAt'].value == "") {
          formGroup.controls['warrantyExpiredAt'].setErrors({ required: true, mustGreaterThanStartDate: false });
        } else {
          formGroup.controls['warrantyExpiredAt'].setErrors(null);
        }
      }
      //purchaseType validation
      if (formGroup.controls['purchaseType'].value == "RENTED") {
        $('.rentalStartAt').removeClass("d-none");
        $('.rentalEndAt').removeClass("d-none");
        $('.rentalStartAt-proxy').addClass("d-none");
        $('.rentalEndAt-proxy').addClass("d-none");
        if (formGroup.controls['rentalStartAt'].value == "" || formGroup.controls['rentalStartAt'].value == null) {
          formGroup.controls['rentalStartAt'].setErrors({ required: true })
        } else {
          if (formGroup.controls['rentalEndAt'].value != "") {
            let startD = moment((formGroup.controls['rentalStartAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
            let endD = moment((formGroup.controls['rentalEndAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
            if (endD <= startD) {
              formGroup.controls['rentalEndAt'].setErrors({ required: false, mustGreaterThanStartDate: true });
            } else {
              formGroup.controls['rentalEndAt'].setErrors(null);
            }
          } else {
            formGroup.controls['rentalStartAt'].setErrors(null);
          }
        }
        if (formGroup.controls['rentalEndAt'].value == "" || formGroup.controls['rentalEndAt'].value == null) {
          formGroup.controls['rentalEndAt'].setErrors({ required: true });
        } else {
          if (formGroup.controls['rentalStartAt'].value != "") {
            let startD = moment((formGroup.controls['rentalStartAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
            let endD = moment((formGroup.controls['rentalEndAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
            if (endD <= startD) {
              formGroup.controls['rentalEndAt'].setErrors({ required: false, mustGreaterThanStartDate: true });
            } else {
              formGroup.controls['rentalEndAt'].setErrors(null);
            }
          } else {
            formGroup.controls['rentalEndAt'].setErrors(null);
          }
        }
      } else {
        $('.rentalStartAt').addClass("d-none");
        $('.rentalEndAt').addClass("d-none");
        $('.rentalStartAt-proxy').removeClass("d-none");
        $('.rentalEndAt-proxy').removeClass("d-none");
        formGroup.controls['rentalStartAt'].setErrors(null);
        formGroup.controls['rentalEndAt'].setErrors(null);
      }
    };
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
      this.miscService
        .departmentsService()
        .subscribe((data: any) => {
          if (typeof data.content !== 'undefined') {
            let departments = data.content;
            this.departmentList = [];
            let c = 0;
            for (let indx = 0; indx < departments.length; indx++) {
              if (departments[indx].status == "ACTIVE") {
                this.departmentList[c++] = Object.assign({ "id": departments[indx].id, "name": departments[indx].name, "version": departments[indx].version });
              }
            }
          }
        });
      this.miscService
        .locationsService()
        .subscribe((data: any) => {
          if (typeof data.content !== 'undefined') {
            let locations = data.content;
            this.locationList = [];
            let c = 0;
            for (let indx = 0; indx < locations.length; indx++) {
              if (locations[indx].status == "ACTIVE") {
                this.locationList[c++] = Object.assign({ "id": locations[indx].id, "name": locations[indx].name, "officeName": locations[indx].officeName, "version": locations[indx].version });
              }
            }
          }
        });
      this.miscService
        .vendorsService({ vendorType: "ASSET" })
        .subscribe((data: any) => {
          let vendors = data.content;
          this.vendorList = [];
          let c = 0;
          for (let indx = 0; indx < vendors.length; indx++) {
            if (String(vendors[indx].status) == 'ACTIVE') {
              this.vendorList[c++] = vendors[indx];
            }
          }
        });
    } else {
      this.miscService
        .departmentsService()
        .subscribe((data: any) => {
          if (typeof data.content !== 'undefined') {
            let departments = data.content;
            this.departmentList = [];
            let c = 0;
            for (let indx = 0; indx < departments.length; indx++) {
              if (departments[indx].status == "ACTIVE") {
                this.departmentList[c++] = Object.assign({ "id": departments[indx].id, "name": departments[indx].name, "version": departments[indx].version });
              }
            }
          }
          this.miscService
            .locationsService()
            .subscribe((data: any) => {
              if (typeof data.content !== 'undefined') {
                let locations = data.content;
                this.locationList = [];
                let c = 0;
                for (let indx = 0; indx < locations.length; indx++) {
                  if (locations[indx].status == "ACTIVE") {
                    this.locationList[c++] = Object.assign({ "id": locations[indx].id, "name": locations[indx].name, "officeName": locations[indx].officeName, "version": locations[indx].version });
                  }
                }
                this.miscService
                  .vendorsService({ vendorType: "ASSET" })
                  .subscribe((data: any) => {
                    let vendors = data.content;
                    this.vendorList = [];
                    let c = 0;
                    for (let indx = 0; indx < vendors.length; indx++) {
                      if (String(vendors[indx].status) == 'ACTIVE') {
                        this.vendorList[c++] = vendors[indx];
                      }
                    }
                  });
              }
            });
        });
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.callAssetService(this.entityId);
    }
  }

  ngAfterViewInit() {
    // this.angForm.get('isDepartment').setValue('1', { onlySelf: true });
    // this.angForm.get('isAvailable').setValue('1', { onlySelf: true });
    // this.angForm.get('isUnderWarranty').setValue('1', { onlySelf: true });
    // this.angForm.get('purchaseType').setValue('OWNED', { onlySelf: true });
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

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
    } else {
      this.assetList = [];
    }
  }

  @ViewChild("vendor") vendor;
  populateVendorDetails(vendor) {
    if (typeof vendor !== 'undefined') {
      for (let k in this.vendorList) {
        if (this.vendorList[k].id == vendor.id) {
          this.angForm.get('vendorName').setValue(this.vendorList[k].contactName);
          this.angForm.get('vendorEmail').setValue(this.vendorList[k].contactEmail);
          this.angForm.get('vendorContactNo').setValue(this.vendorList[k].contactNumber);
          break;
        }
      }
    } else {
      this.angForm.get('vendorName').setValue("");
      this.angForm.get('vendorEmail').setValue("");
      this.angForm.get('vendorContactNo').setValue("");
    }
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      this.asset.isAvailable = true;
      this.asset.isDepartment = this.angForm.controls['isDepartment'].value == 1 ? true : false;
      if (this.asset.isDepartment) {
        for (let k in this.departmentList) {
          if (this.angForm.controls['department'].value == this.departmentList[k].id) {
            this.asset.department = { id: this.departmentList[k].id, name: this.departmentList[k].name, version: this.departmentList[k].version };
          }
        }
        this.asset.location = null;
      } else {
        this.asset.department = null;
        for (let k in this.locationList) {
          if (this.angForm.controls['location'].value == this.locationList[k].id) {
            this.asset.location = { id: this.locationList[k].id, name: this.locationList[k].name, version: this.locationList[k].version };
          }
        }
      }
      for (let k in this.locationList) {
        if (this.angForm.controls['raisedForLocation'].value == this.locationList[k].id) {
          this.asset.raisedForLocation = { id: this.locationList[k].id, name: this.locationList[k].name, version: this.locationList[k].version };
        }
      }
      this.asset.productCategory = this.angForm.controls['productCategory'].value;
      this.asset.assetType = this.angForm.controls['assetType'].value;
      this.asset.modelNo = this.angForm.controls['modelNo'].value;
      this.asset.serialNo = this.angForm.controls['serialNumber'].value;
      this.asset.isUnderWarranty = this.angForm.controls['isUnderWarranty'].value == 1 ? true : false;
      this.asset.purchaseType = PurchaseType[this.angForm.controls['purchaseType'].value];
      this.asset.warrantyExpiredAt = AppUtility.prepareDateToString(moment(this.angForm.controls['warrantyExpiredAt'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      this.asset.rentalStartAt = this.asset.purchaseType == 0 ? AppUtility.prepareDateToString(moment(this.angForm.controls['warrantyExpiredAt'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate()) : null;
      this.asset.rentalEndAt = this.asset.purchaseType == 0 ? AppUtility.prepareDateToString(moment(this.angForm.controls['warrantyExpiredAt'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate()) : null;
      for (let k in this.vendorList) {
        if (this.angForm.controls['vendor'].value == this.vendorList[k].id) {
          this.asset.vendor = { id: this.vendorList[k].id, name: this.vendorList[k].name, version: this.vendorList[k].version };
        }
      }
      this.asset.comment = this.angForm.controls['comment'].value;
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
    this.assetService
      .saveAsset(this.asset)
      .subscribe((data: any) => {
        this.router.navigate(['/asset/inventory']);
      });
  }

  callAssetService(id: number) {
    this.assetService
      .assetService(id)
      .subscribe((data: any) => {
        this.asset = data;
        this.asset.raisedUser = { id: this.asset.raisedUser.id, version: this.asset.raisedUser.version };
        this.asset.organisation = { id: this.asset.organisation.id, version: this.asset.organisation.version };
        this.angForm.get('productCategory').setValue(this.asset.productCategory);
        this.populateChildValues(this.asset.productCategory);
        this.angForm.get('assetType').setValue(this.asset.assetType);
        //this.populateAssetModels(this.asset.assetType);
        this.angForm.get('modelNo').setValue(this.asset.modelNo);
        this.angForm.get('serialNumber').setValue(this.asset.serialNo);
        this.angForm.get('isDepartment').setValue(this.asset.isDepartment ? "1" : "0");
        if (this.asset.isDepartment == true) {
          $('#dept-inp').removeClass("d-none");
          $('#loc-inp').addClass("d-none");
          this.asset.location = null;
          this.angForm.get('department').setValue(String(this.asset.department.id));
        } else {
          $('#dept-inp').addClass("d-none");
          $('#loc-inp').removeClass("d-none");
          this.asset.department = null;
          this.angForm.get('location').setValue(String(this.asset.location.id));
        }
        this.angForm.get('raisedForLocation').setValue(String(this.asset.raisedForLocation.id));
        this.angForm.get('isUnderWarranty').setValue(this.asset.isUnderWarranty ? "1" : "0");
        this.angForm.get('warrantyExpiredAt').setValue(moment(this.asset.warrantyExpiredAt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
        this.angForm.get('purchaseType').setValue(this.asset.purchaseType);
        if (this.asset.purchaseType != "OWNED") {
          this.angForm.get('rentalStartAt').setValue(moment(this.asset.rentalStartAt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
          this.angForm.get('rentalEndAt').setValue(moment(this.asset.rentalEndAt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
          $('.rentalStartAt').removeClass("d-none");
          $('.rentalEndAt').removeClass("d-none");
          $('.rentalStartAt-proxy').addClass("d-none");
          $('.rentalEndAt-proxy').addClass("d-none");
        } else {
          $('.rentalStartAt').addClass("d-none");
          $('.rentalEndAt').addClass("d-none");
          $('.rentalStartAt-proxy').removeClass("d-none");
          $('.rentalEndAt-proxy').removeClass("d-none");
        }
        this.angForm.get('vendor').setValue(String(this.asset.vendor.id));
        this.angForm.get('comment').setValue(String(this.asset.comment));
      });
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}