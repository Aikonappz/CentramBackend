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
import { AssetOrder } from '../../model/AssetOrder';
import { AssetOrderService } from '../../service/AssetOrderService';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { CommonAlert } from '../../containers/default-layout/modal/CommonAlert';
declare var $: any;

@Component({
  selector: 'app-asset-order',
  templateUrl: './asset-order.component.html',
  styleUrls: ['./asset-order.component.scss']
})
export class AssetOrderComponent implements OnInit {
  moduleName: string = "ORDER ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  newEntity: boolean = true;
  defaultStatus: any = 'OPEN';
  statusFlag: boolean = true;
  entityId: number;
  angForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  approver1List: any[] = [];
  departmentList: any[] = [];
  locationList: any[] = [];
  vendorList: any[] = [];

  purchaseTypeList: any[] = [];
  rentDurationList: any[] = [];
  assetOrder: AssetOrder;

  booleanList: any[] = [];
  assetList: any[] = [];
  modelList: any[] = [];
  assetModelList: any[] = [];
  productTypes: any[] = [];
  currencyList: any[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
    private assetOrderService: AssetOrderService,
    private miscService: MiscService,
    private userService: UserService,
    private mediaService: MediaService,
    private clientStorageService: ClientStorageService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.booleanList.push({ id: 0, label: 'No' });
    this.booleanList.push({ id: 1, label: 'Yes' });
    this.purchaseTypeList.push({ id: 'RENTED', label: 'RENTED' });
    this.purchaseTypeList.push({ id: 'OWNED', label: 'OWNED' });
    this.rentDurationList.push({ id: '1-2 Years', label: '1-2 Years' });
    this.rentDurationList.push({ id: '2-4 Years', label: '2-4 Years' });
    this.rentDurationList.push({ id: 'More than 5 years', label: 'More than 5 years' });
    this.currencyList.push({ id: 'INR', label: 'INR' });
    this.currencyList.push({ id: 'USD', label: 'USD' });
    this.angForm = this.fb.group({
      isDepartment: new FormControl(null, [
        Validators.required,
      ]),
      department: new FormControl(null, [
      ]),
      location: new FormControl(null, [
      ]),
      productCategory: new FormControl(null, [
        Validators.required,
      ]),
      assetType: new FormControl(null, [
        Validators.required,
      ]),
      quantity: new FormControl('', [
        Validators.required,
        Validators.pattern("^[0-9]*$"),
        Validators.maxLength(4),
      ]),
      modelNo: new FormControl(null, [
        //Validators.required,
      ]),
      modelNoTxt: new FormControl(null, [
      ]),
      currency: new FormControl(null, [
        Validators.required,
      ]),
      limitAmount: new FormControl('', [
      ]),
      extraAmount: new FormControl('', [
      ]),
      totalAmount: new FormControl(null, [
        Validators.required,
        Validators.pattern("^[0-9]*$"),
      ]),
      withinBudget: new FormControl(null, [
        Validators.required,
      ]),
      vendor: new FormControl(null, [
        Validators.required,
      ]),
      existingAgreement: new FormControl(null, [
      ]),
      agreementEndAt: new FormControl('', [
        Validators.required,
      ]),
      purchaseType: new FormControl(null, [
        Validators.required,
      ]),
      rentDuration: new FormControl(null, [
      ]),
      rentStartAt: new FormControl('', [
      ]),
      rentEndAt: new FormControl('', [
      ]),
      otherDetails: new FormControl(null, [
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
    }, {
      validators: this.customValidations(),
    });
    this.assetOrder = new AssetOrder();
  }

  getToday(): string {
    return new Date().toISOString().split('T')[0]
  }

  getSelectedLabel(id: number) {
    if (id != null) {
      for (let indx = 0; indx < this.vendorList.length; indx++) {
        if (this.vendorList[indx].id == id) {
          return this.vendorList[indx].name;
        }
      }
    }
    return null;
  }

  customValidations() {
    return (formGroup: FormGroup) => {
      if (formGroup.controls['productCategory'].value == 158) {
        if (formGroup.controls['modelNoTxt'].value == null || formGroup.controls['modelNoTxt'].value == "") {
          //$('#modelNoTxt').removeClass("d-none");
          //$('#modelNo').addClass("d-none");
          //formGroup.controls['modelNo'].setErrors(null);
          //formGroup.controls['modelNoTxt'].setErrors({ required: true });
        } else {
          //$('#modelNoTxt').addClass("d-none");
          //$('#modelNo').removeClass("d-none");
          //formGroup.controls['modelNoTxt'].setErrors(null);
        }
      } else {
        //$('#modelNoTxt').addClass("d-none");
        //$('#modelNo').removeClass("d-none");
        //formGroup.controls['modelNoTxt'].setErrors(null);
      }
      if (formGroup.controls['existingAgreement'].value == null) {
        formGroup.controls['existingAgreement'].setErrors({ required: true, notValidAgreement: false });
      } else {
        if (formGroup.controls['existingAgreement'].value == '0' && formGroup.controls['vendor'].value > 0) {
          formGroup.controls['existingAgreement'].setErrors({ required: false, notValidAgreement: true });
        } else {
          formGroup.controls['existingAgreement'].setErrors(null);
        }
      }
      //console.log(formGroup.controls['vendor']);
      let selectedVendor = this.getSelectedLabel(formGroup.controls['vendor'].value);
      if (selectedVendor != null && selectedVendor != 'Others') {
        $('.proxy-agreement-end-date-col').addClass("d-none");
        $('.agreement-end-date').removeClass("d-none");
        if (formGroup.controls['agreementEndAt'].value != null) {
          formGroup.controls['agreementEndAt'].setErrors(null);
        } else {
          formGroup.controls['agreementEndAt'].setErrors({ required: true, });
        }
      } else {
        $('.agreement-end-date').addClass("d-none");
        $('.proxy-agreement-end-date-col').removeClass("d-none");
        formGroup.controls['agreementEndAt'].setErrors(null);
      }
      if (formGroup.controls['purchaseType'].value == "RENTED") {
        $('.proxy-duration-col').addClass("d-none");
        $('.duration-col').removeClass("d-none");
        if (formGroup.controls['rentDuration'].value != null) {
          formGroup.controls['rentDuration'].setErrors(null);
        } else {
          formGroup.controls['rentDuration'].setErrors({ required: true, });
        }
      } else {
        $('.duration-col').addClass("d-none");
        $('.proxy-duration-col').removeClass("d-none");
        formGroup.controls['rentDuration'].setErrors(null);
      }
      /*if (formGroup.controls['purchaseType'].value == "RENTED") {
        $('.proxy-rent').addClass("d-none");
        $('.rent').removeClass("d-none");
        if (formGroup.controls['rentStartAt'].value == "" || formGroup.controls['rentStartAt'].value == null) {
          formGroup.controls['rentStartAt'].setErrors({ required: true, mustGreaterThanStartDate: false });
        } else {
          formGroup.controls['rentStartAt'].setErrors(null);
        }
        if (formGroup.controls['rentEndAt'].value == "" || formGroup.controls['rentEndAt'].value == null) {
          formGroup.controls['rentEndAt'].setErrors({ required: true, mustGreaterThanStartDate: false });
        } else {
          if (formGroup.controls['rentStartAt'].value != "" && formGroup.controls['rentStartAt'].value != null) {
            let startD = moment((formGroup.controls['rentStartAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
            let endD = moment((formGroup.controls['rentEndAt'].value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
            if (endD <= startD) {
              formGroup.controls['rentEndAt'].setErrors({ required: false, mustGreaterThanStartDate: true });
            } else {
              formGroup.controls['rentEndAt'].setErrors(null);
            }
          } else {
            formGroup.controls['rentEndAt'].setErrors(null);
          }
        }
      } else {
        $('.proxy-rent').removeClass("d-none");
        $('.rent').addClass("d-none");
        formGroup.controls['rentStartAt'].setErrors(null);
        formGroup.controls['rentEndAt'].setErrors(null);
      }*/
      if (formGroup.controls['withinBudget'].value == "0") {
        if (formGroup.controls['limitAmount'].value == "" || formGroup.controls['limitAmount'].value == null) {
          formGroup.controls['limitAmount'].setErrors({ required: true, maxlength: false, pattern: false });
        } else {
          if (formGroup.controls['limitAmount'].value.length > 10) {
            formGroup.controls['limitAmount'].setErrors({ required: false, maxlength: true, pattern: false });
          } else {
            if (/^\d+(\.\d{1,2})?$/.test(formGroup.controls['limitAmount'].value)) {
              formGroup.controls['limitAmount'].setErrors(null);
            } else {
              formGroup.controls['limitAmount'].setErrors({ required: false, maxlength: false, pattern: true });
            }
          }
        }
        if (formGroup.controls['extraAmount'].value == "" || formGroup.controls['extraAmount'].value == null) {
          formGroup.controls['extraAmount'].setErrors({ required: true, maxlength: false });
        } else {
          if (formGroup.controls['extraAmount'].value.length > 10) {
            formGroup.controls['extraAmount'].setErrors({ required: false, maxlength: true });
          } else {
            if (/^\d+(\.\d{1,2})?$/.test(formGroup.controls['extraAmount'].value)) {
              formGroup.controls['extraAmount'].setErrors(null);
            } else {
              formGroup.controls['extraAmount'].setErrors({ required: false, maxlength: false, pattern: true });
            }
          }
        }
        $('.proxy-col').addClass("d-none");
        $('.extraAmount').removeClass("d-none");
        $('.limitAmount').removeClass("d-none");
      } else {
        $('.extraAmount').addClass("d-none");
        $('.limitAmount').addClass("d-none");
        $('.proxy-col').removeClass("d-none");
        formGroup.controls['extraAmount'].setErrors(null);
        formGroup.controls['limitAmount'].setErrors(null);
      }
      if (formGroup.controls['isDepartment'].value == "1") {
        if (formGroup.controls['department'].value == "" || formGroup.controls['department'].value == null) {
          formGroup.controls['department'].setErrors({ required: true });
        } else {
          formGroup.controls['location'].setErrors(null);
          formGroup.controls['department'].setErrors(null);
        }
      } else if (formGroup.controls['isDepartment'].value == "0") {
        if (formGroup.controls['location'].value == "" || formGroup.controls['location'].value == null) {
          formGroup.controls['location'].setErrors({ required: true })
        } else {
          formGroup.controls['department'].setErrors(null);
          formGroup.controls['location'].setErrors(null);
        }
      } else {
        formGroup.controls['department'].setErrors(null);
        formGroup.controls['location'].setErrors(null);
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
                this.locationList[c++] = Object.assign({ "id": locations[indx].id, "name": locations[indx].officeName, "version": locations[indx].version });
              }
            }
          }
        });
      this.miscService
        .vendorsService({ vendorType: "ASSET" })
        .subscribe((data: any) => {
          let vendors = data.content;
          this.vendorList = [];
          //this.vendorList.push({ id: 0, name: "Others" });
          //let c = 1;
          let c = 0;
          for (let indx = 0; indx < vendors.length; indx++) {
            if (String(vendors[indx].status) == 'ACTIVE') {
              this.vendorList[c++] = vendors[indx];
            }
          }
        });
      this.userService
        .getUsersService({ size: AppUtility.MAX_PAGE_SIZE })
        .subscribe((data: any) => {
          let users = data.content;
          this.approver1List = [];
          for (let k in users) {
            if (users[k].roleNames.includes('ORG_ASSET_ORDER_APPROVER')) {
              this.approver1List.push(users[k]);
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
    } else {
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      // this.callIncidentService(this.entityId);
    }
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      this.assetOrder.isDepartment = this.angForm.controls['isDepartment'].value == 1 ? true : false;
      if (this.assetOrder.isDepartment) {
        for (let k in this.departmentList) {
          if (this.angForm.controls['department'].value == this.departmentList[k].id) {
            this.assetOrder.department = { id: this.departmentList[k].id, name: this.departmentList[k].name, version: this.departmentList[k].version };
          }
        }
        this.assetOrder.location = null;
      } else {
        this.assetOrder.department = null;
        for (let k in this.locationList) {
          if (this.angForm.controls['location'].value == this.locationList[k].id) {
            this.assetOrder.location = { id: this.locationList[k].id, name: this.locationList[k].name, version: this.locationList[k].version };
          }
        }
      }
      this.assetOrder.moduleId = this.angForm.controls['productCategory'].value;
      this.assetOrder.currency = this.angForm.controls['currency'].value;
      this.assetOrder.subModuleId = this.angForm.controls['assetType'].value;
      this.assetOrder.quantity = this.angForm.controls['quantity'].value;
      this.assetOrder.totalAmount = this.angForm.controls['totalAmount'].value;
      this.assetOrder.otherDetails = this.angForm.controls['otherDetails'].value;
      //console.log(this.angForm.controls['modelNoTxt'].value);
      let modelNo = null;
      if (this.assetOrder.moduleId == 158) {
        modelNo = this.angForm.controls['modelNoTxt'].value;
      } else {
        modelNo = this.angForm.controls['modelNo'].value;
      }
      this.assetOrder.model = modelNo == "" || modelNo == null ? null : modelNo;
      this.assetOrder.withinBudget = this.angForm.controls['withinBudget'].value == 1 ? true : false;
      if (!this.assetOrder.withinBudget) {
        this.assetOrder.limitAmount = this.angForm.controls['limitAmount'].value;
        this.assetOrder.extraAmount = this.angForm.controls['extraAmount'].value;
      } else {
        this.assetOrder.limitAmount = null;
        this.assetOrder.extraAmount = null;
      }
      for (let k in this.approver1List) {
        if (this.angForm.controls['approverUser1'].value == this.approver1List[k].id) {
          this.assetOrder.approverUser1 = { id: this.approver1List[k].id, firstName: this.approver1List[k].firstName, lastName: this.approver1List[k].lastName, email: this.approver1List[k].email, version: this.approver1List[k].version };
        }
      }
      for (let k in this.approver1List) {
        if (this.angForm.controls['approverUser2'].value == this.approver1List[k].id) {
          this.assetOrder.approverUser2 = { id: this.approver1List[k].id, firstName: this.approver1List[k].firstName, lastName: this.approver1List[k].lastName, email: this.approver1List[k].email, version: this.approver1List[k].version };
        }
      }
      this.assetOrder.purchaseType = PurchaseType[this.angForm.controls['purchaseType'].value];
      this.assetOrder.existingAgreement = this.angForm.controls['existingAgreement'].value == 1 ? true : false;
      let selectedVendor = this.getSelectedLabel(this.angForm.controls['vendor'].value);
      if (selectedVendor != null && selectedVendor != 'Others') {
        if (!this.assetOrder.existingAgreement) {
          return;
        } else {
          this.assetOrder.agreementEndAt = AppUtility.prepareDateToDateTimeString(moment(this.angForm.controls['agreementEndAt'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
        }
      } else {
        this.assetOrder.agreementEndAt = null;
      }
      if (this.angForm.controls['vendor'].value > 0) {
        for (let k in this.vendorList) {
          if (this.angForm.controls['vendor'].value == this.vendorList[k].id) {
            this.assetOrder.vendor = { id: this.vendorList[k].id, name: this.vendorList[k].name, version: this.vendorList[k].version };
          }
        }
      } else {
        this.assetOrder.vendor = null;
      }
      if (this.angForm.controls['purchaseType'].value == 'RENTED') {
        this.assetOrder.rentDuration = this.angForm.controls['rentDuration'].value;
      } else {
        this.assetOrder.rentDuration = null;
      }
      /*if (this.angForm.controls['purchaseType'].value == 'RENTED') {
        this.assetOrder.rentStartAt = AppUtility.prepareDateToString(moment(this.angForm.controls['rentStartAt'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
        this.assetOrder.rentEndAt = AppUtility.prepareDateToString(moment(this.angForm.controls['rentEndAt'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      }*/
      //console.log(AssetType[this.angForm.controls['assetType'].value]);
      //console.log(JSON.stringify(this.assetOrder));
      this.callSaveAssetOrderService(this.assetOrder);
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this.router.navigate(['/asset/ordered']);
  }

  callSaveAssetOrderService(assetOrder: AssetOrder) {
    this.assetOrderService
      .saveAssetOrder(assetOrder)
      .subscribe((data: any) => {
        const config: ModalOptions = {
          backdrop: 'static',
          keyboard: false,
          animated: true,
          ignoreBackdropClick: true,
          class: 'modal-bg',
        };
        const initialState = {
          msg: "Your order has been successfully submitted.",
          url: "/asset/ordered"
        };
        this.modalRef = this.modalService.show(CommonAlert, Object.assign({}, config, { initialState }));
        //this.router.navigate(['/asset/ordered']);
      });
  }

  @ViewChild("isDepartment") isDepartment;
  isDepartmentChange(isDepartment) {
    if (typeof isDepartment !== 'undefined') {
      $(function () {
        if (isDepartment.id == '0') {
          $('#dept-inp').addClass("d-none");
          $('#loc-inp').removeClass("d-none");
        } else if (isDepartment.id == '1') {
          $('#dept-inp').removeClass("d-none");
          $('#loc-inp').addClass("d-none");
        }
      });
    }
  }

  @ViewChild("productCategory") productCategory;
  populateChildValues(productCategory) {
    if (typeof productCategory !== 'undefined') {
      this.assetList = [];
      for (let i = 0; i < this.assetModelList.length; i++) {
        if (this.assetModelList[i].parentModuleId == productCategory.id
          && this.assetModelList[i].assetOPSName != null) {
          this.assetList.push({ id: this.assetModelList[i].id, label: AppUtility.toTitleCase(this.assetModelList[i].assetOPSName) });
        }
      }
      if (productCategory.label == "Infra Assets") {
        $('#modelNoTxt').removeClass("d-none");
        $('#modelNo').addClass("d-none");
      } else {
        $('#modelNoTxt').addClass("d-none");
        $('#modelNo').removeClass("d-none");
      }
      this.angForm.controls['assetType'].setValue(null);
    } else {
      $('#modelNoTxt').addClass("d-none");
      $('#modelNo').removeClass("d-none");
      this.assetList = [];
      this.modelList = [];
      this.angForm.controls['assetType'].setValue(null);
      this.angForm.controls['modelNo'].setValue(null);
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
    }
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

}