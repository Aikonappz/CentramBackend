import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../../service/MiscService';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { Status } from '../../../model/enumerator/Status';
import { TicketAllocationType } from '../../../model/enumerator/TicketAllocationType';
import { Account } from '../../../model/Account';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { LicenseType } from '../../../model/enumerator/LicenseType';
declare var $: any;

@Component({
  selector: 'app-editaccount',
  templateUrl: './editaccount.component.html',
  styleUrls: ['./editaccount.component.scss']
})
export class EditAccountComponent implements OnInit {
  moduleName: string = "ACCOUNT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  numRegex = /^\d+$/;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  account: Account;
  angForm: FormGroup;
  ticketAllocationTypes: any[] = [];
  loggedInUser: LoggedInUser;
  accountType: any = 'ALL';
  accountTypes: any[] = [];

  constructor(
    private fb: FormBuilder,
    private loggedInUserService: LoggedInUserService,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private miscService: MiscService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.account = new Account();
    this.account.status = this.defaultStatus;
    this.account.ticketAllocationType = TicketAllocationType.GENERIC;
    this.account.contractHours = "00:00";

    let ticketAllocationTypes = Object.values(TicketAllocationType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));
    for (let k in ticketAllocationTypes) {
      this.ticketAllocationTypes.push({ id: ticketAllocationTypes[k], label: ticketAllocationTypes[k] });
    }
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    let accountTypes = Object.values(LicenseType)
      .filter((value) => typeof value === "string" && value != "ALL" && ((this.loggedInUser.licenseType != 'ALL' && this.loggedInUser.licenseType == value) || this.loggedInUser.licenseType == 'ALL'))
      .map((value) => (value as string));
    for (let k in accountTypes) {
      this.accountTypes.push({ id: accountTypes[k], label: accountTypes[k] });
    }
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
    this.route.params.subscribe(params => {
      //this.type = this.route.snapshot.paramMap.get('licenceType');
      if (!this.route.snapshot.paramMap.has('id')) {
      } else {
        this.newEntity = false;
        this.entityId = Number(this.route.snapshot.paramMap.get('id'));
        this.callVendorService(this.entityId);
      }
      this.angForm = this.fb.group({
        type: new FormControl(null, [
          Validators.required,
          Validators.maxLength(255),
        ]),
      });
    });
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      if (this.statusFlag === false) {
        let res = window.confirm("Do you really want to change the status?")
        if (!res) {
          return;
        }
      }
      //console.log(this.angForm);
      this.account.accountType = LicenseType[this.angForm.controls['type'].value];
      this.account.name = this.angForm.controls['name'].value;
      this.account.contactAddress = this.angForm.controls['contactAddress'].value;
      this.account.contactEmail = this.angForm.controls['contactEmail'].value;
      this.account.contactName = this.angForm.controls['contactName'].value;
      this.account.contactNumber = this.angForm.controls['contactNumber'].value;
      this.account.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(JSON.stringify(this.account));
      if (this.accountType != "UAT") {
        this.account.contractHours = this.angForm.controls['contractHours'].value;
        this.account.ticketAllocationType = this.angForm.controls['ticketAllocationType'].value == "" ? null : this.angForm.controls['ticketAllocationType'].value;
      }
      this.account.organisation = null;
      //console.log(this.angForm.controls['status'].value);
      this.callSaveAccountService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveAccountService() {
    this.miscService
      .saveAccountService(this.account)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/uat/master/account']);
      });
  }

  callVendorService(id: number) {
    this.miscService
      .accountService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.account.id = data.id;
        this.account.name = data.name;
        this.account.accountNo = data.accountNo;
        this.account.contactAddress = data.contactAddress;
        this.account.contactEmail = data.contactEmail;
        this.account.contactName = data.contactName;
        this.account.contactNumber = data.contactNumber;
        this.account.status = data.status;
        this.account.contractHours = data.contractHours;
        this.account.version = data.version;
        this.account.ticketAllocationType = data.ticketAllocationType;
        this.account.accountType = data.accountType;
        this.driveFormByType({ id: this.account.accountType, label: this.account.accountType });
        //console.log(JSON.stringify(this.user));
        this.angForm.get('type').setValue(this.account.accountType);
        this.angForm.get('name').setValue(this.account.name);
        this.angForm.get('contactAddress').setValue(this.account.contactAddress);
        this.angForm.get('contactEmail').setValue(this.account.contactEmail);
        this.angForm.get('contactName').setValue(this.account.contactName);
        this.angForm.get('contactNumber').setValue(this.account.contactNumber);
        if (this.account.accountType != 'UAT') {
          this.angForm.get('ticketAllocationType').setValue(this.account.ticketAllocationType);
          this.angForm.get('contractHours').setValue(this.account.contractHours);
        }
        this.statusFlag = String(this.account.status) == 'ACTIVE' ? true : false;
        //this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }



  @ViewChild("type") type;
  driveFormByType(type) {
    if (typeof type !== 'undefined') {
      this.accountType = type.id;
      //console.log(type);
      if (this.accountType != 'UAT') {
        this.angForm = this.fb.group({
          type: new FormControl(this.accountType, [
            Validators.required,
            Validators.maxLength(255),
          ]),
          name: new FormControl(null, [
            Validators.required,
            Validators.maxLength(255),
          ]),
          contactName: new FormControl(null, [
            Validators.required,
            Validators.maxLength(255),
          ]),
          contactEmail: new FormControl(null, [
            Validators.required,
            Validators.maxLength(255),
            Validators.email,
          ]),
          contractHours: new FormControl(null, [
            Validators.maxLength(255),
            Validators.pattern(this.numRegex),
          ]),
          contactNumber: new FormControl(null, [
            Validators.required,
            Validators.pattern(this.phoneRegex),
          ]),
          contactAddress: new FormControl(null, [
            Validators.required,
          ]),
          ticketAllocationType: new FormControl(null, [
            Validators.required,
          ]),
          status: new FormControl('ACTIVE', [
          ]),
        });
      } else {
        this.angForm = this.fb.group({
          type: new FormControl(this.accountType, [
            Validators.required,
            Validators.maxLength(255),
          ]),
          name: new FormControl(null, [
            Validators.required,
            Validators.maxLength(255),
          ]),
          contactName: new FormControl(null, [
            Validators.required,
            Validators.maxLength(255),
          ]),
          contactEmail: new FormControl(null, [
            Validators.required,
            Validators.maxLength(255),
            Validators.email,
          ]),
          // contractHours: new FormControl(null, [
          //   Validators.maxLength(255),
          //   Validators.pattern(this.numRegex),
          // ]),
          contactNumber: new FormControl(null, [
            Validators.required,
            Validators.pattern(this.phoneRegex),
          ]),
          contactAddress: new FormControl(null, [
            Validators.required,
          ]),
          // ticketAllocationType: new FormControl(null, [
          //   Validators.required,
          // ]),
          status: new FormControl('ACTIVE', [
          ]),
        });
      }

    } else {
      this.accountType = "ALL";
      this.angForm = this.fb.group({
        type: new FormControl(null, [
          Validators.required,
          Validators.maxLength(255),
        ]),
      });
    }
  }

}