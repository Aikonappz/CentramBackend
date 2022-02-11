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
import { AssetType } from '../../model/enumerator/AssetType';
import { PurchaseType } from '../../model/enumerator/PurchaseType';
import { AssetOrder } from '../../model/AssetOrder';
import { AssetOrderService } from '../../service/AssetOrderService';
declare var $: any;

@Component({
  selector: 'app-addorder',
  templateUrl: './addorder.component.html',
  styleUrls: ['./addorder.component.scss']
})
export class AddOrderComponent implements OnInit {
  moduleName: string = "ORDER ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'OPEN';
  statusFlag: boolean = true;
  entityId: number;
  angForm: FormGroup;
  hasAgentPermission: boolean;
  approver1List: any[] = [];
  approver2List: any[] = [];
  departmentList: any[] = [];
  locationList: any[] = [];
  vendorList: any[] = [];
  assetList: any[] = [];
  purchaseTypeList: any[] = [];
  assetOrder: AssetOrder;

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
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      isDepartment: new FormControl('', [
      ]),
      department: new FormControl('', [
      ]),
      location: new FormControl('', [
      ]),
      assetType: new FormControl('', [
        Validators.required,
      ]),
      quantity: new FormControl('', [
        Validators.required,
        Validators.pattern("^[0-9]*$"),
        Validators.maxLength(4),
      ]),
      cost: new FormControl('', [
        Validators.required,
        Validators.pattern(/^\d+(\.\d{1,2})?$/),
        Validators.maxLength(10),
      ]),
      withinBudget: new FormControl('', [
      ]),
      approverUser1: new FormControl('', [
        Validators.required,
      ]),
      approverUser2: new FormControl('', [
        Validators.required,
      ]),
      purchaseType: new FormControl('', [
        Validators.required,
      ]),
      existingAgreement: new FormControl('', [
      ]),
      vendor: new FormControl('', [
        Validators.required,
      ]),
      comment: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
      ]),
    });
    this.assetOrder = new AssetOrder();
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

    this.assetList = Object.values(AssetType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));

    this.purchaseTypeList = Object.values(PurchaseType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));

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
        .vendorsService()
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
      this.userService
        .getUsersService({ size: 2147483647 })
        .subscribe((data: any) => {
          let users = data.content;
          for (let k in users) {
            if (users[k].roleNames.includes('ORG_OUTBOUND_ASSET_APPROVER')) {
              this.approver1List.push(users[k]);
              this.approver2List.push(users[k]);
            }
          }
        });
    } else {
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      // this.callIncidentService(this.entityId);
    }
  }

  ngAfterViewInit() {
    this.angForm.get('isDepartment').setValue('1', { onlySelf: true });
    this.angForm.get('existingAgreement').setValue('1', { onlySelf: true });
    this.angForm.get('withinBudget').setValue('1', { onlySelf: true });
  }

  ngAfterContentInit() {
    $(function () {
      $('#isDepartment').on('change', function () {
        if ($(this).val() == 1) {
          $('#dept-inp').removeClass("d-none");
          $('#loc-inp').addClass("d-none");
        } else {
          $('#dept-inp').addClass("d-none");
          $('#loc-inp').removeClass("d-none");
        }
      });
    });
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      if ($('#isDepartment').val() == 1) {
        if ($('#department').val() == "") {
          $('#dept-err').removeClass('d-none');
          return false;
        } else {
          $('#dept-err').addClass('d-none');
        }
      } else {
        if ($('#location').val() == "") {
          $('#loc-err').removeClass('d-none');
          return false;
        } else {
          $('#loc-err').addClass('d-none');
        }
      }
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
      this.assetOrder.assetType = AssetType[this.angForm.controls['assetType'].value];
      this.assetOrder.quantity = this.angForm.controls['quantity'].value;
      this.assetOrder.cost = this.angForm.controls['cost'].value;
      this.assetOrder.withinBudget = this.angForm.controls['withinBudget'].value == 1 ? true : false;
      for (let k in this.approver1List) {
        if (this.angForm.controls['approverUser1'].value == this.approver1List[k].id) {
          this.assetOrder.approverUser1 = { id: this.approver1List[k].id, email: this.approver1List[k].email, version: this.approver1List[k].version };
        }
      }
      for (let k in this.approver2List) {
        if (this.angForm.controls['approverUser2'].value == this.approver2List[k].id) {
          this.assetOrder.approverUser2 = { id: this.approver2List[k].id, email: this.approver1List[k].email, version: this.approver2List[k].version };
        }
      }
      this.assetOrder.purchaseType = PurchaseType[this.angForm.controls['purchaseType'].value];
      this.assetOrder.existingAgreement = this.angForm.controls['existingAgreement'].value == 1 ? true : false;
      for (let k in this.vendorList) {
        if (this.angForm.controls['vendor'].value == this.vendorList[k].id) {
          this.assetOrder.vendor = { id: this.vendorList[k].id, name: this.vendorList[k].name, version: this.vendorList[k].version };
        }
      }
      this.assetOrder.comment = this.angForm.controls['comment'].value;
      console.log(JSON.stringify(this.assetOrder));
      this.callSaveIncidentService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this._location.back();
  }

  callSaveIncidentService() {
    this.assetOrderService
      .saveAssetOrder(this.assetOrder)
      .subscribe((data: any) => {
        this.router.navigate(['/asset/order']);
      });
  }

  callIncidentService(id: number) {
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

}