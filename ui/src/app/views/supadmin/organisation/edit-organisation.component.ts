import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { LocationVO } from '../../../model/LocationVO';
import { Role } from '../../../model/Role';
import { Department } from '../../../model/Department';
import { MiscService } from '../../../service/MiscService';
import { User } from '../../../model/User';
import { Status } from '../../../model/enumerator/Status';
import { LicenseType, } from '../../../model/enumerator/LicenseType';
import { Organisation } from '../../../model/Organisation';
import { OrganisationService } from '../../../service/OrganisationService';
import { UserVO } from '../../../model/UserVO';
import * as moment from 'moment-timezone';
import { AppUtility } from '../../../config/AppUtility';
import { StartEndDateValidation } from '../../../validator/StartEndDateValidation';
import { Setting } from '../../../model/Setting';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { ContactPerson } from '../../../model/ContactPerson';
declare var $: any;

@Component({
  selector: 'app-edit-organisation',
  templateUrl: './edit-organisation.component.html',
  styleUrls: ['./edit-organisation.component.scss']
})
export class EditOrganisationComponent implements OnInit {
  moduleName: string = "ORGANIZATION";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  licenseTypes: any[] = [];
  panRegex = /^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}?$/;
  tanRegex = /^([a-zA-Z]){4}([0-9]){5}([a-zA-Z]){1}?$/;
  gstinRegex = /\d{2}[A-Z]{5}\d{4}[A-Z]{1}[A-Z\d]{1}[Z]{1}[A-Z\d]{1}/;
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  org: Organisation;
  user: User;
  roles: Role[];
  locations: LocationVO[];
  departments: Department[];
  angForm: FormGroup;
  oldStatus: boolean = false;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private orgService: OrganisationService,
    private miscService: MiscService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.user = new User();
    this.user.status = this.defaultStatus;
    Object.values(LicenseType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string))
      .forEach((item, index) => {
        this.licenseTypes.push({ value: item, label: item, });
      });
    this.org = new Organisation();
    this.org.status = this.defaultStatus;
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
    if (!this.route.snapshot.paramMap.has('id')) {
      this.angForm = this.fb.group({
        name: new FormControl('', [
          Validators.required,
          Validators.maxLength(255)
        ]),
        add1: new FormControl('', [
          Validators.required,
          Validators.maxLength(255)
        ]),
        add2: new FormControl('', [
          Validators.maxLength(255)
        ]),
        city: new FormControl('', [
          Validators.required,
          Validators.maxLength(255)
        ]),
        pincode: new FormControl('', [
          Validators.required,
          Validators.maxLength(10)
        ]),
        pan: new FormControl('', [
          //Validators.required,
          Validators.pattern(this.panRegex),
          //Validators.required
        ]),
        tan: new FormControl('', [
          //Validators.required,
          Validators.pattern(this.tanRegex),
          //Validators.required
        ]),
        gstin: new FormControl('', [
          //Validators.required,
          Validators.pattern(this.gstinRegex),
          //Validators.required
        ]),
        licenseType: new FormControl(null, [
          Validators.required
        ]),
        licenseStart: new FormControl('', [
          Validators.required
        ]),
        licenseEnd: new FormControl('', [
          Validators.required
        ]),
        status: new FormControl('ACTIVE', [

        ]),
        firstName: new FormControl('', [
          Validators.required,
          Validators.maxLength(255),
        ]),
        lastName: new FormControl('', [
          Validators.required,
          Validators.maxLength(255),
        ]),
        email: new FormControl('', [
          Validators.required,
          Validators.email,
          Validators.maxLength(255),
        ]),
        contactNo: new FormControl('', [
          Validators.required,
          Validators.pattern(this.phoneRegex),
        ]),
        secContactNo: new FormControl('', [
          Validators.pattern(this.phoneRegex),
        ]),
        employeeId: new FormControl('NA', [
          //Validators.required,
          Validators.maxLength(255),
        ]),
        projectCode: new FormControl('NA', [
          //Validators.required
          Validators.maxLength(255),
        ]),
        department: new FormControl('', [
          //Validators.required
        ]),
        location: new FormControl('', [
          //Validators.required
        ]),
        keyPersonName1: new FormControl('', [
          Validators.required,
          Validators.maxLength(255),
        ]),
        keyPersonEmail1: new FormControl('', [
          Validators.required,
          Validators.email,
          Validators.maxLength(255),
        ]),
        keyPersonContact1: new FormControl('', [
          Validators.required,
          Validators.pattern(this.phoneRegex),
          Validators.maxLength(255),
        ]),
        keyPersonName2: new FormControl('', [
          //Validators.required
          Validators.maxLength(255),
        ]),
        keyPersonEmail2: new FormControl('', [
          //Validators.required
          Validators.email,
          Validators.maxLength(255),
        ]),
        keyPersonContact2: new FormControl('', [
          //Validators.required
          Validators.pattern(this.phoneRegex),
          Validators.maxLength(255),
        ]),
      }, {
        validators: StartEndDateValidation('licenseStart', 'licenseEnd')
      });
      // this.miscService
      //   .locationsService()
      //   .subscribe((data: any) => {
      //     //console.log("load locations");
      //     this.locations = data.content;
      //   });
      // this.miscService
      //   .departmentsService()
      //   .subscribe((data: any) => {
      //     //console.log("load departments");
      //     this.departments = data.content;
      //   });
    } else {
      this.angForm = this.fb.group({
        name: new FormControl('', [
          Validators.required,
          Validators.maxLength(255)
        ]),
        add1: new FormControl('', [
          Validators.required,
          Validators.maxLength(255)
        ]),
        add2: new FormControl('', [
          Validators.maxLength(255)
        ]),
        city: new FormControl('', [
          Validators.required,
          Validators.maxLength(255)
        ]),
        pincode: new FormControl('', [
          Validators.required,
          Validators.maxLength(10)
        ]),
        pan: new FormControl('', [
          //Validators.required,
          Validators.pattern(this.panRegex),
          //Validators.required
        ]),
        tan: new FormControl('', [
          //Validators.required,
          Validators.pattern(this.tanRegex),
          //Validators.required
        ]),
        gstin: new FormControl('', [
          //Validators.required,
          Validators.pattern(this.gstinRegex),
          //Validators.required
        ]),
        licenseType: new FormControl(null, [
          Validators.required
        ]),
        licenseStart: new FormControl('', [
          Validators.required
        ]),
        licenseEnd: new FormControl('', [
          Validators.required
        ]),
        status: new FormControl('ACTIVE', [

        ]),
        firstName: new FormControl('', [
        ]),
        lastName: new FormControl('', [
        ]),
        email: new FormControl('', [
        ]),
        contactNo: new FormControl('', [
        ]),
        secContactNo: new FormControl('', [
          Validators.pattern(this.phoneRegex),
        ]),
        employeeId: new FormControl('NA', [
        ]),
        projectCode: new FormControl('NA', [
        ]),
        department: new FormControl('', [
        ]),
        location: new FormControl('', [
        ]),
        keyPersonName1: new FormControl('', [
          Validators.required,
          Validators.maxLength(255),
        ]),
        keyPersonEmail1: new FormControl('', [
          Validators.required,
          Validators.email,
          Validators.maxLength(255),
        ]),
        keyPersonContact1: new FormControl('', [
          Validators.required,
          Validators.pattern(this.phoneRegex),
          Validators.maxLength(255),
        ]),
        keyPersonName2: new FormControl('', [
          //Validators.required
          Validators.maxLength(255),
        ]),
        keyPersonEmail2: new FormControl('', [
          //Validators.required
          Validators.email,
          Validators.maxLength(255),
        ]),
        keyPersonContact2: new FormControl('', [
          //Validators.required
          Validators.pattern(this.phoneRegex),
          Validators.maxLength(255),
        ]),
      }, {
        validators: StartEndDateValidation('licenseStart', 'licenseEnd')
      });
      if (this.route.snapshot.paramMap.has('id')) {
        if (!Number.isNaN(this.route.snapshot.paramMap.get('id'))) {
          //console.log("load provided id data...");
          this.entityId = Number(this.route.snapshot.paramMap.get('id'));
          this.newEntity = false;
          this.callGetUserService(this.entityId);
        }
      }
      // this.miscService
      //   .locationsService()
      //   .subscribe((data: any) => {
      //     //console.log("load locations");
      //     this.locations = data.content;
      //     this.miscService
      //       .departmentsService()
      //       .subscribe((data: any) => {
      //         //console.log("load departments");
      //         this.departments = data.content;
      //         if (this.route.snapshot.paramMap.has('id')) {
      //           if (!Number.isNaN(this.route.snapshot.paramMap.get('id'))) {
      //             //console.log("load provided id data...");
      //             this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      //             this.newEntity = false;
      //             this.callGetUserService(this.entityId);
      //           }
      //         }
      //       });
      //   });
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  initSelectBoxes() {
  }

  formSubmit() {
    if (this.angForm.valid) {
      if (!this.newEntity && this.statusFlag != this.oldStatus) {
        let res = window.confirm("Do you really want to change the status?")
        if (!res) {
          return;
        }
      }
      //console.log(this.angForm);
      this.org.name = this.angForm.controls['name'].value;
      this.org.add1 = this.angForm.controls['add1'].value;
      this.org.add2 = this.angForm.controls['add2'].value;
      this.org.city = this.angForm.controls['city'].value;
      this.org.pincode = this.angForm.controls['pincode'].value;
      this.org.pan = this.angForm.controls['pan'].value;
      this.org.tan = this.angForm.controls['tan'].value;
      this.org.gstin = this.angForm.controls['gstin'].value;
      this.org.licenseType = LicenseType[String(this.angForm.controls['licenseType'].value)];
      this.org.licenseStart = AppUtility.prepareDateToString(moment(this.angForm.controls['licenseStart'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      this.org.licenseEnd = AppUtility.prepareDateToString(moment(this.angForm.controls['licenseEnd'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      this.org.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      let cnt1 = new ContactPerson();
      cnt1.name = this.angForm.controls['keyPersonName1'].value;
      cnt1.email = this.angForm.controls['keyPersonEmail1'].value;
      cnt1.contactNo = this.angForm.controls['keyPersonContact1'].value;
      let cnt2 = new ContactPerson();
      cnt2.name = this.angForm.controls['keyPersonName2'].value;
      cnt2.email = this.angForm.controls['keyPersonEmail2'].value;
      cnt2.contactNo = this.angForm.controls['keyPersonContact2'].value;
      this.org.contactPersons = [];
      this.org.contactPersons.push(cnt1);
      this.org.contactPersons.push(cnt2);
      //console.log(JSON.stringify(this.org));
      if (this.newEntity) {
        this.org.setting = new Setting();
        this.user.firstName = this.angForm.controls['firstName'].value;
        this.user.lastName = this.angForm.controls['lastName'].value;
        this.user.email = this.angForm.controls['email'].value;
        this.user.roles = [3];
        this.user.contactNo = this.angForm.controls['contactNo'].value;
        this.user.secContactNo = this.angForm.controls['secContactNo'].value;
        this.user.employeeId = this.angForm.controls['employeeId'].value;
        this.user.projectCode = this.angForm.controls['projectCode'].value;
        this.user.employeeId = this.user.employeeId == null ? null : this.user.employeeId.replace(/\s/g, "");
        this.user.projectCode = this.user.projectCode == null ? null : this.user.projectCode.replace(/\s/g, "")
        this.user.organisation = null;
        this.user.employeeId = "EMP0001";
        /* process department and location */
        let location = this.angForm.controls['location'].value;
        let department = this.angForm.controls['department'].value;
        this.user.location = null;
        this.user.department = null;
        if (location != "") {
          let loc = new LocationVO();
          loc.version = location.split("__")[1];
          loc.id = location.split("__")[0];
          this.user.location = loc;
        }
        if (department != "") {
          let dept = new Department();
          dept.version = department.split("__")[1];
          dept.id = department.split("__")[0];
          this.user.department = dept;
        }
        /* process department and location */
        this.user.status = Status['ACTIVE'];
        //console.log(JSON.stringify(this.user));
      }
      this.callSaveOrganisationService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveOrganisationService() {
    this.orgService
      .saveOrganisationService(this.org)
      .subscribe((data: Organisation) => {
        //console.log(JSON.stringify(data)); 
        if (this.newEntity) {
          this.callAddUserService(data.id, data.version);
        }
        this.router.navigate(['/supadmin/organization']);
      });
  }

  callAddUserService(id: number, version: number) {
    this.user.organisation = new Organisation();
    this.user.organisation.id = id;
    this.user.status = Status['ACTIVE'];
    this.user.organisation.version = version;
    this.user.organisation.status = Status['ACTIVE'];
    this.user.vendor = null;
    this.userService
      .saveUserService(this.user)
      .subscribe((data: UserVO) => {
        //console.log(data);        
      });
  }

  callGetUserService(id: number) {
    this.orgService
      .getOrganisationService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.org.id = data.id;
        this.org.version = data.version;
        this.org.name = data.name;
        this.org.pan = data.pan;
        this.org.tan = data.tan;
        this.org.gstin = data.gstin;
        this.org.add1 = data.add1;
        this.org.add2 = data.add2;
        this.org.city = data.city;
        this.org.pincode = data.pincode;
        this.org.status = data.status;
        this.org.licenseEnd = data.licenseEnd;
        this.org.licenseStart = data.licenseStart;
        this.org.licenseType = data.licenseType;
        this.org.contactPersons = data.contactPersons;
        this.org.setting = data.setting == null ? new Setting() : data.setting;
        //console.log(JSON.stringify(this.org));
        console.log(this.org.licenseType);
        this.angForm.get('licenseType').setValue(this.org.licenseType);
        this.angForm.get('licenseStart').setValue(moment(this.org.licenseStart).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
        this.angForm.get('licenseEnd').setValue(moment(this.org.licenseEnd).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
        this.angForm.get('pincode').setValue(this.org.pincode);
        this.angForm.get('city').setValue(this.org.city);
        this.angForm.get('add2').setValue(this.org.add2);
        this.angForm.get('add1').setValue(this.org.add1);
        this.angForm.get('gstin').setValue(this.org.gstin);
        this.angForm.get('pan').setValue(this.org.pan);
        this.angForm.get('tan').setValue(this.org.tan);
        this.angForm.get('name').setValue(this.org.name);
        this.statusFlag = String(this.org.status) == 'ACTIVE' ? true : false;
        this.oldStatus = String(this.org.status) == 'ACTIVE' ? true : false;
        this.angForm.get('keyPersonName1').setValue(this.org.contactPersons[0].name);
        this.angForm.get('keyPersonEmail1').setValue(this.org.contactPersons[0].email);
        this.angForm.get('keyPersonContact1').setValue(this.org.contactPersons[0].contactNo);
        this.angForm.get('keyPersonName2').setValue(this.org.contactPersons[1].name);
        this.angForm.get('keyPersonEmail2').setValue(this.org.contactPersons[1].email);
        this.angForm.get('keyPersonContact2').setValue(this.org.contactPersons[1].contactNo);
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.markAllAsTouched();
        //this.initSelectBoxes();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }

}