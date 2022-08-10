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
  modelList: any[] = [];
  assetModelList: any[] = [];
  productTypes: any[] = [];
  booleanList: any[] = [];
  approver1List: any[] = [];
  requesterList: any[] = [];

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
      modelNo: new FormControl(null, [
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
      approverUser1: new FormControl(null, [
        Validators.required,
      ]),
      approver1Name: new FormControl('', [
      ]),
      approver1Email: new FormControl('', [
      ]),
      approver1contact: new FormControl('', [
      ]),
      approverUser2: new FormControl(null, [
        Validators.required,
      ]),
      approver2Name: new FormControl('', [
      ]),
      approver2Email: new FormControl('', [
      ]),
      approver2contact: new FormControl('', [
      ]),
      orderRequestedUser: new FormControl(null, [
        Validators.required,
      ]),
      orderRequestedName: new FormControl('', [
      ]),
      orderRequestedEmail: new FormControl('', [
      ]),
      orderRequestedcontact: new FormControl('', [
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
        if (this.assetModelList[k].id == formGroup.controls['assetType'].value) {
          if (this.assetModelList[k].generateAssetNo == false && (formGroup.controls['serialNumber'].value == "")) {
            formGroup.controls['serialNumber'].setErrors({ required: true });
            break;
          } else if (this.assetModelList[k].generateAssetNo == false && (formGroup.controls['serialNumber'].value != "")) {
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
      this.userService
        .getUsersService({ size: AppUtility.MAX_PAGE_SIZE })
        .subscribe((data: any) => {
          let users = data.content;
          this.requesterList = [];
          this.approver1List = [];
          for (let k in users) {
            users[k].nameWithId = users[k].employeeId + "/" + users[k].fullName;
            if (users[k].roleNames.includes('ORG_ASSET_ORDER_APPROVER')) {
              this.approver1List.push(users[k]);
            }
            if (users[k].roleNames.includes('ORG_ASSET_ADMIN')) {
              this.requesterList.push(users[k]);
            }
          }
        });
      this.miscService
        .modulesService({ licenseType: 'ASSET' })
        .subscribe((data: any) => {
          this.assetModelList = data.content;
          this.productTypes = [];
          for (let k in this.assetModelList) {
            if (this.assetModelList[k].status == "ACTIVE"
              && this.assetModelList[k].appModule == false
              && this.assetModelList[k].parentModuleId == null
              && this.assetModelList[k].assetOPSName != null)
              this.productTypes.push({ id: this.assetModelList[k].id, label: AppUtility.toTitleCase(this.assetModelList[k].assetOPSName) });
          }
          //console.log(this.productTypes);
        });
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
                    this.userService
                      .getUsersService({ size: AppUtility.MAX_PAGE_SIZE })
                      .subscribe((data: any) => {
                        let users = data.content;
                        this.requesterList = [];
                        this.approver1List = [];
                        for (let k in users) {
                          users[k].nameWithId = users[k].employeeId + "/" + users[k].fullName;
                          if (users[k].roleNames.includes('ORG_ASSET_ORDER_APPROVER')) {
                            this.approver1List.push(users[k]);
                          }
                          if (users[k].roleNames.includes('ORG_ASSET_ADMIN')) {
                            this.requesterList.push(users[k]);
                          }
                        }
                        this.miscService
                          .modulesService({ licenseType: 'ASSET' })
                          .subscribe((data: any) => {
                            this.assetModelList = data.content;
                            this.productTypes = [];
                            for (let k in this.assetModelList) {
                              if (this.assetModelList[k].status == "ACTIVE" && this.assetModelList[k].appModule == false && this.assetModelList[k].parentModuleId == null && this.assetModelList[k].assetOPSName != null)
                                this.productTypes.push({ id: this.assetModelList[k].id, label: AppUtility.toTitleCase(this.assetModelList[k].assetOPSName) });
                            }
                            //console.log(this.productTypes);
                            this.newEntity = false;
                            this.entityId = Number(this.route.snapshot.paramMap.get('id'));
                            this.callAssetService(this.entityId);
                          });
                      });
                  });
              }
            });
        });
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
      for (let i = 0; i < this.assetModelList.length; i++) {
        if (this.assetModelList[i].parentModuleId == productCategory.id && this.assetModelList[i].assetOPSName != null) {
          this.assetList.push({ id: this.assetModelList[i].id, label: AppUtility.toTitleCase(this.assetModelList[i].assetOPSName) });
        }
      }
      this.angForm.controls['assetType'].setValue(null);
    } else {
      this.angForm.controls['assetType'].setValue(null);
    }
  }

  @ViewChild("assetType") assetType;
  populateModels(assetType) {
    if (typeof assetType !== 'undefined') {
      this.modelList = [];
      for (let i = 0; i < this.assetModelList.length; i++) {
        if (this.assetModelList[i].id == assetType.id) {
          for (let k = 0; k < this.assetModelList[i].models.length; k++) {
            this.modelList.push({ id: this.assetModelList[i].models[k], label: this.assetModelList[i].models[k] });
          }
        }
      }
      this.angForm.controls['modelNo'].setValue(null);
    } else {
      this.angForm.controls['modelNo'].setValue(null);
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
      this.asset.isLocation = this.angForm.controls['forLocation'].value == 1 ? true : false;
      if (this.asset.isLocation) {
        for (let k in this.locationList) {
          if (this.angForm.controls['raisedForLocation'].value == this.locationList[k].id) {
            this.asset.raisedForLocation = { id: this.locationList[k].id, name: this.locationList[k].name, version: this.locationList[k].version };
          }
        }
      }
      this.asset.moduleId = this.angForm.controls['productCategory'].value;
      this.asset.subModuleId = this.angForm.controls['assetType'].value;
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
      for (let k in this.approver1List) {
        if (this.angForm.controls['approverUser1'].value == this.approver1List[k].id) {
          this.asset.approverUser1 = { id: this.approver1List[k].id, firstName: this.approver1List[k].firstName, lastName: this.approver1List[k].lastName, email: this.approver1List[k].email, version: this.approver1List[k].version };
        }
      }
      for (let k in this.approver1List) {
        if (this.angForm.controls['approverUser2'].value == this.approver1List[k].id) {
          this.asset.approverUser2 = { id: this.approver1List[k].id, firstName: this.approver1List[k].firstName, lastName: this.approver1List[k].lastName, email: this.approver1List[k].email, version: this.approver1List[k].version };
        }
      }
      for (let k in this.requesterList) {
        if (this.angForm.controls['orderRequestedUser'].value == this.requesterList[k].id) {
          this.asset.orderRequestedUser = { id: this.requesterList[k].id, firstName: this.requesterList[k].firstName, lastName: this.requesterList[k].lastName, email: this.requesterList[k].email, version: this.requesterList[k].version };
        }
      }
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
        this.asset.orderRequestedUser = { id: this.asset.orderRequestedUser.id, version: this.asset.orderRequestedUser.version };
        this.asset.organisation = { id: this.asset.organisation.id, version: this.asset.organisation.version };
        this.angForm.get('productCategory').setValue(this.asset.moduleId);
        this.populateChildValues({ id: this.asset.moduleId });
        this.populateModels({ id: this.asset.subModuleId });
        this.angForm.get('assetType').setValue(this.asset.subModuleId);
        this.angForm.get('modelNo').setValue(this.asset.modelNo);
        this.angForm.get('serialNumber').setValue(this.asset.serialNo);
        this.angForm.get('isDepartment').setValue(this.asset.isDepartment ? 1 : 0);
        if (this.asset.isDepartment == true) {
          $('#dept-inp').removeClass("d-none");
          $('#loc-inp').addClass("d-none");
          this.asset.location = null;
          this.angForm.get('department').setValue(this.asset.department.id);
        } else {
          $('#dept-inp').addClass("d-none");
          $('#loc-inp').removeClass("d-none");
          this.asset.department = null;
          this.angForm.get('location').setValue(this.asset.location.id);
        }
        this.angForm.get('forLocation').setValue(this.asset.isLocation ? 1 : 0);
        if (this.asset.isLocation == true) {
          $('#loc-inp').removeClass("d-none");
          $('#proxy-lic-inp').addClass("d-none");
          this.angForm.get('raisedForLocation').setValue(this.asset.raisedForLocation.id);
        } else {
          $('#loc-inp').addClass("d-none");
          $('#proxy-lic-inp').removeClass("d-none");
        }
        this.angForm.get('isUnderWarranty').setValue(this.asset.isUnderWarranty ? 1 : 0);
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
        this.angForm.get('vendor').setValue(this.asset.vendor.id);
        this.populateVendorDetails({ id: this.asset.vendor.id });
        this.angForm.get('approverUser2').setValue(this.asset.approverUser2.id);
        this.approverUser2Populate({ id: this.asset.approverUser2.id });
        this.angForm.get('approverUser1').setValue(this.asset.approverUser1.id);
        this.approverUser1Populate({ id: this.asset.approverUser1.id });
        this.angForm.get('orderRequestedUser').setValue(this.asset.orderRequestedUser.id);
        this.requesterUserPopulate({ id: this.asset.orderRequestedUser.id });
      });
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  @ViewChild("approverUser1") approverUser1;
  approverUser1Populate(approverUser1) {
    if (typeof approverUser1 !== 'undefined') {
      for (let i in this.approver1List) {
        if (approverUser1.id == this.approver1List[i].id) {
          this.angForm.controls['approver1Name'].setValue(this.approver1List[i].fullName);
          this.angForm.controls['approver1Email'].setValue(this.approver1List[i].email);
          this.angForm.controls['approver1contact'].setValue(this.approver1List[i].contactNo);
        }
      }
    } else {
      this.angForm.controls['approver1Name'].setValue("");
      this.angForm.controls['approver1Email'].setValue("");
      this.angForm.controls['approver1contact'].setValue("");
    }
  }

  @ViewChild("approverUser2") approverUser2;
  approverUser2Populate(approverUser2) {
    if (typeof approverUser2 !== 'undefined') {
      for (let i in this.approver1List) {
        if (approverUser2.id == this.approver1List[i].id) {
          this.angForm.controls['approver2Name'].setValue(this.approver1List[i].fullName);
          this.angForm.controls['approver2Email'].setValue(this.approver1List[i].email);
          this.angForm.controls['approver2contact'].setValue(this.approver1List[i].contactNo);
        }
      }
    } else {
      this.angForm.controls['approver2Name'].setValue("");
      this.angForm.controls['approver2Email'].setValue("");
      this.angForm.controls['approver2contact'].setValue("");
    }
  }


  @ViewChild("orderRequestedUser") orderRequestedUser;
  requesterUserPopulate(orderRequestedUser) {
    if (typeof orderRequestedUser !== 'undefined') {
      for (let i in this.approver1List) {
        if (orderRequestedUser.id == this.requesterList[i].id) {
          this.angForm.controls['orderRequestedName'].setValue(this.requesterList[i].fullName);
          this.angForm.controls['orderRequestedEmail'].setValue(this.requesterList[i].email);
          this.angForm.controls['orderRequestedcontact'].setValue(this.requesterList[i].contactNo);
        }
      }
    } else {
      this.angForm.controls['orderRequestedName'].setValue("");
      this.angForm.controls['orderRequestedEmail'].setValue("");
      this.angForm.controls['orderRequestedcontact'].setValue("");
    }
  }

}