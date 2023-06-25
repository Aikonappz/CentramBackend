import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { Status } from '../../model/enumerator/Status';
import { TicketAllocationType } from '../../model/enumerator/TicketAllocationType';
import { Account } from '../../model/Account';
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
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  account: Account;
  angForm: FormGroup;
  ticketAllocationTypes: any[] = [];
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
    let ticketAllocationTypes = Object.values(TicketAllocationType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));
    for (let k in ticketAllocationTypes) {
      this.ticketAllocationTypes.push({ id: ticketAllocationTypes[k], label: ticketAllocationTypes[k] });
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
      this.account.name = this.angForm.controls['name'].value;
      this.account.contactAddress = this.angForm.controls['contactAddress'].value;
      this.account.contactEmail = this.angForm.controls['contactEmail'].value;
      this.account.contactName = this.angForm.controls['contactName'].value;
      this.account.contactNumber = this.angForm.controls['contactNumber'].value;
      this.account.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      this.account.ticketAllocationType = this.angForm.controls['ticketAllocationType'].value == "" ? null : this.angForm.controls['ticketAllocationType'].value;
      this.account.organisation = null;
      //console.log(this.vendor);
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
        this.router.navigate(['/master/account']);
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
        this.account.version = data.version;
        this.account.ticketAllocationType = data.ticketAllocationType;
        //console.log(JSON.stringify(this.user));
        this.angForm.get('name').setValue(this.account.name);
        this.angForm.get('contactAddress').setValue(this.account.contactAddress);
        this.angForm.get('contactEmail').setValue(this.account.contactEmail);
        this.angForm.get('contactName').setValue(this.account.contactName);
        this.angForm.get('contactNumber').setValue(this.account.contactNumber);
        this.angForm.get('ticketAllocationType').setValue(this.account.ticketAllocationType);
        this.statusFlag = String(this.account.status) == 'ACTIVE' ? true : false;
        //this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }
}