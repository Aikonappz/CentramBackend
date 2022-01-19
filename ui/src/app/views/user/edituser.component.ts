import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { LocationVO } from '../../model/LocationVO';
import { Role } from '../../model/Role';
import { Department } from '../../model/Department';
import { MiscService } from '../../service/MiscService';
import { User } from '../../model/User';
import { Status } from '../../model/enumerator/Status';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { Vendor } from '../../model/Vendor';
import { LoggedInUser } from '../../model/LoggedInUser';
declare var $: any;

@Component({
  selector: 'app-edituser',
  templateUrl: './edituser.component.html',
  styleUrls: ['./edituser.component.scss']
})
export class EditUserComponent implements OnInit {
  moduleName: string = "USER";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  user: User;
  roles: Role[];
  locations: LocationVO[];
  departments: Department[];
  users: User[];
  roleList: any[];
  locationList: any[];
  departmentList: any[];
  usrList: any[];
  vendorList: any[];
  c: number = 0;
  rolesList: string[];
  loggedInUser: LoggedInUser;
  angForm: FormGroup;
  constructor(
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private miscService: MiscService,
    private loggedInUserService: LoggedInUserService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.user = new User();
    this.user.status = this.defaultStatus;
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.rolesList = this.loggedInUser.roles;

    if (this.rolesList.includes('APP_ADMIN')) {
      this.angForm = new FormGroup({
        firstName: new FormControl('', [
          Validators.required,
        ]),
        lastName: new FormControl('', [
          Validators.required,
        ]),
        email: new FormControl('', [
          Validators.required,
          Validators.email,
        ]),
        contactNo: new FormControl('', [
          Validators.required,
          Validators.pattern(this.phoneRegex),
        ]),
        secContactNo: new FormControl('', [
          Validators.pattern(this.phoneRegex),
        ]),
        employeeId: new FormControl('NA', [
          Validators.required,
        ]),
        managerId: new FormControl('NA', [
          //Validators.required,
        ]),
        projectCode: new FormControl('NA', [
          //Validators.required
        ]),
        roles: new FormControl('', [
          Validators.required
        ]),
        department: new FormControl('', [
          //Validators.required
        ]),
        location: new FormControl('', [
          //Validators.required
        ]),
        vendorId: new FormControl('', [
          //Validators.required
        ]),
        status: new FormControl(true, [

        ]),
        userType: new FormControl(true, [

        ]),
      });
    } else {
      this.angForm = new FormGroup({
        firstName: new FormControl('', [
          Validators.required,
        ]),
        lastName: new FormControl('', [
          Validators.required,
        ]),
        email: new FormControl('', [
          Validators.required,
          Validators.email,
        ]),
        contactNo: new FormControl('', [
          Validators.required,
          Validators.pattern(this.phoneRegex),
        ]),
        secContactNo: new FormControl('', [
          Validators.pattern(this.phoneRegex),
        ]),
        employeeId: new FormControl('NA', [
          Validators.required,
        ]),
        managerId: new FormControl('NA', [
          //Validators.required,
        ]),
        projectCode: new FormControl('NA', [
          //Validators.required
        ]),
        roles: new FormControl('', [
          Validators.required
        ]),
        department: new FormControl('', [
          //Validators.required
        ]),
        location: new FormControl('', [
          Validators.required
        ]),
        vendorId: new FormControl('', [
          //Validators.required
        ]),
        status: new FormControl(true, [

        ]),
        userType: new FormControl(true, [

        ]),
      });
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
    if (!this.route.snapshot.paramMap.has('id')) {
      this.miscService
        .rolesService()
        .subscribe((data: any) => {
          //console.log("load roles");
          this.roles = data.content;
          let tmpRoles: any = [];
          this.c = 0;
          for (let i = 0; i < this.roles.length; i++) {
            if (this.loggedInUserService.appManager()) {
              if (!this.roles[i].name.match(/ORG_.*/)) {
                tmpRoles[this.c++] = this.roles[i];
              }
            } else {
              if (this.roles[i].name.match(/ORG_.*/)) {
                tmpRoles[this.c++] = this.roles[i];
              }
            }
          }
          this.roles = tmpRoles;
          this.c = 0;
          for (let indx = 0; indx < this.roles.length; indx++) {
            if (this.roles[indx].status == 1) {
              this.roleList[this.c++] = Object.assign({ "id": this.roles[indx].id, "name": this.roles[indx].name });
            }
          }
        });

      if (!this.loggedInUserService.appManager()) {
        this.miscService
          .locationsService()
          .subscribe((data: any) => {
            //console.log("load locations");
            if (typeof data.content !== 'undefined') {
              this.locations = data.content;
              this.c = 0;
              for (let indx = 0; indx < this.locations.length; indx++) {
                if (this.locations[indx].status == 1) {
                  this.locationList[this.c++] = Object.assign({ "id": this.locations[indx].id, "name": this.locations[indx].name });
                }
              }
            }
          });
        this.miscService
          .departmentsService()
          .subscribe((data: any) => {
            //console.log("load departments");
            if (typeof data.content !== 'undefined') {
              this.departments = data.content;
              this.c = 0;
              for (let indx = 0; indx < this.departments.length; indx++) {
                if (this.departments[indx].status == 1) {
                  this.departmentList[this.c++] = Object.assign({ "id": this.departments[indx].id, "name": this.departments[indx].name });
                }
              }
            }
          });
        this.miscService
          .vendorsService()
          .subscribe((data: any) => {
            let vendors = data.content;
            this.c = 0;
            this.vendorList = [];
            for (let indx = 0; indx < vendors.length; indx++) {
              if (String(vendors[indx].status) == 'ACTIVE') {
                this.vendorList[this.c++] = vendors[indx];
                //Object.assign({ "id": vendors[indx].id, "version": vendors[indx].version, "name": vendors[indx].name });
              }
            }
          });
      }
      this.userService
        .getUsersService()
        .subscribe((data: any) => {
          //console.log("load departments");
          this.users = data.content;
          this.c = 0;
          this.usrList = [];
          for (let indx = 0; indx < this.users.length; indx++) {
            if (String(this.users[indx].status) == 'ACTIVE' && this.users[indx].employeeId != "") {
              this.usrList[this.c++] = Object.assign({ "id": this.users[indx].id, "name": this.users[indx].employeeId });
            }
          }
          this.preapareSelect();
          //console.log(JSON.stringify(this.usrList));
        });
    } else {
      if (this.loggedInUserService.appManager()) {
        this.miscService
          .rolesService()
          .subscribe((data: any) => {
            //console.log("load roles");
            this.roles = data.content;
            let tmpRoles: any = [];
            this.c = 0;
            for (let i = 0; i < this.roles.length; i++) {
              if (this.loggedInUserService.appManager()) {
                if (!this.roles[i].name.match(/ORG_.*/)) {
                  tmpRoles[this.c++] = this.roles[i];
                }
              } else {
                if (this.roles[i].name.match(/ORG_.*/)) {
                  tmpRoles[this.c++] = this.roles[i];
                }
              }
            }
            this.roles = tmpRoles;
            this.c = 0;
            for (let indx = 0; indx < this.roles.length; indx++) {
              if (this.roles[indx].status == 1) {
                this.roleList[this.c++] = Object.assign({ "id": this.roles[indx].id, "name": this.roles[indx].name });
              }
            }
            this.userService
              .getUsersService()
              .subscribe((data: any) => {
                //console.log("load departments");
                this.users = data.content;
                this.c = 0;
                this.usrList = [];
                for (let indx = 0; indx < this.users.length; indx++) {
                  if (String(this.users[indx].status) == 'ACTIVE' && this.users[indx].employeeId != "") {
                    this.usrList[this.c++] = Object.assign({ "id": this.users[indx].id, "name": this.users[indx].employeeId });
                  }
                }
                if (this.route.snapshot.paramMap.has('id')) {
                  if (!Number.isNaN(this.route.snapshot.paramMap.get('id'))) {
                    //console.log("load provided id data...");
                    this.entityId = Number(this.route.snapshot.paramMap.get('id'));
                    this.newEntity = false;
                    this.callGetUserService(this.entityId);
                  }
                }
              });
          });
      } else {
        this.miscService
          .rolesService()
          .subscribe((data: any) => {
            //console.log("load roles");
            this.roles = data.content;
            let tmpRoles: any = [];
            this.c = 0;
            for (let i = 0; i < this.roles.length; i++) {
              if (this.loggedInUserService.appManager()) {
                if (!this.roles[i].name.match(/ORG_.*/)) {
                  tmpRoles[this.c++] = this.roles[i];
                }
              } else {
                if (this.roles[i].name.match(/ORG_.*/)) {
                  tmpRoles[this.c++] = this.roles[i];
                }
              }
            }
            this.roles = tmpRoles;
            this.c = 0;
            for (let indx = 0; indx < this.roles.length; indx++) {
              if (this.roles[indx].status == 1) {
                this.roleList[this.c++] = Object.assign({ "id": this.roles[indx].id, "name": this.roles[indx].name });
              }
            }
            this.miscService
              .locationsService()
              .subscribe((data: any) => {
                //console.log("load locations");
                if (typeof data.content !== 'undefined') {
                  this.locations = data.content;
                  this.c = 0;
                  for (let indx = 0; indx < this.locations.length; indx++) {
                    if (this.locations[indx].status == 1) {
                      this.locationList[this.c++] = Object.assign({ "id": this.locations[indx].id, "name": this.locations[indx].name });
                    }
                  }
                }
                this.miscService
                  .departmentsService()
                  .subscribe((data: any) => {
                    //console.log("load departments");
                    if (typeof data.content !== 'undefined') {
                      this.departments = data.content;
                      this.c = 0;
                      for (let indx = 0; indx < this.departments.length; indx++) {
                        if (this.departments[indx].status == 1) {
                          this.departmentList[this.c++] = Object.assign({ "id": this.departments[indx].id, "name": this.departments[indx].name });
                        }
                      }
                    }
                    this.userService
                      .getUsersService()
                      .subscribe((data: any) => {
                        //console.log("load departments");
                        this.users = data.content;
                        this.c = 0;
                        this.usrList = [];
                        for (let indx = 0; indx < this.users.length; indx++) {
                          if (String(this.users[indx].status) == 'ACTIVE' && this.users[indx].employeeId != "") {
                            this.usrList[this.c++] = Object.assign({ "id": this.users[indx].id, "name": this.users[indx].employeeId });
                          }
                        }
                        this.miscService
                          .vendorsService()
                          .subscribe((data: any) => {
                            let vendors = data.content;
                            this.c = 0;
                            this.vendorList = [];
                            for (let indx = 0; indx < vendors.length; indx++) {
                              if (String(vendors[indx].status) == 'ACTIVE') {
                                this.vendorList[this.c++] = vendors[indx];
                                //Object.assign({ "id": vendors[indx].id, "version": vendors[indx].version, "name": vendors[indx].name });
                              }
                            }
                          });
                        if (this.route.snapshot.paramMap.has('id')) {
                          if (!Number.isNaN(this.route.snapshot.paramMap.get('id'))) {
                            //console.log("load provided id data...");
                            this.entityId = Number(this.route.snapshot.paramMap.get('id'));
                            this.newEntity = false;
                            this.callGetUserService(this.entityId);
                          }
                        }
                      });
                  });
              });
          });
      }
    }

    $(function () {
      $('#userType').change(function () {
        if ($(this).val() == 'Employee') {
          $('.vendor-col').addClass('d-none');
          $('#vendorId').val('');
        } else if ($(this).val() == 'Agent') {
          $('.vendor-col').removeClass('d-none');
        }
      });
    });
  }

  preapareSelect() {
    $(document).ready(function () {
      $('#managerId').selectize({
        maxItems: 1,
        onItemAdd: function (value, $item) {
        },
        onItemRemove: function (value) {
        }
      });
    })
  }

  ngAfterViewInit() { }

  ngAfterContentInit() {
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    //console.log(this.angForm);
    let managerId = $('#managerId').val();
    if (managerId == '') {
      $('#managerId-error').removeClass('d-none');
      return false;
    } else {
      $('#managerId-error').addClass('d-none');
    }
    //console.log(managerId);
    if (this.angForm.valid) {
      if (this.statusFlag === false) {
        let res = window.confirm("Do you really want to change the status?")
        if (!res) {
          return;
        }
      }
      //console.log(this.angForm);
      this.user.firstName = this.angForm.controls['firstName'].value;
      this.user.lastName = this.angForm.controls['lastName'].value;
      this.user.email = this.angForm.controls['email'].value;
      this.user.contactNo = this.angForm.controls['contactNo'].value;
      this.user.secContactNo = this.angForm.controls['secContactNo'].value;
      this.user.employeeId = this.angForm.controls['employeeId'].value;
      this.user.managerId = managerId;
      this.user.projectCode = this.angForm.controls['projectCode'].value;
      this.user.projectCode = this.angForm.controls['projectCode'].value;
      this.user.roles = this.angForm.controls['roles'].value;
      this.user.employeeId = this.user.employeeId == null ? null : this.user.employeeId.replace(/\s/g, "");
      this.user.projectCode = this.user.projectCode == null ? null : this.user.projectCode.replace(/\s/g, "")
      this.user.organisation = null;
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
      let vendorId = this.angForm.controls['vendorId'].value;
      if (vendorId != '' && this.angForm.controls['userType'].value == 'Agent') {
        let vendor = new Vendor();
        for (let i in this.vendorList) {
          if (this.vendorList[i].id == vendorId) {
            vendor = this.vendorList[i];
            vendor.organisation = null;
            vendor.status = Status.ACTIVE;
            break;
          }
        }
        //console.log(vendor);
        this.user.vendor = vendor;
      } else {
        this.user.vendor = null;
      }

      /* process department and location */
      this.user.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user);
      this.callSaveUserService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveUserService() {
    this.userService
      .saveUserService(this.user)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/user']);
      });
  }

  callGetUserService(id: number) {
    this.userService
      .getUserService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.user.id = data.id;
        this.user.firstName = data.firstName;
        this.user.lastName = data.lastName;
        this.user.email = data.email;
        this.user.contactNo = data.contactNo;
        this.user.employeeId = data.employeeId;
        this.user.managerId = data.managerId;
        this.user.projectCode = data.projectCode;
        this.user.roles = data.roles;
        this.user.organisation.id = data.organisationId;
        this.user.version = data.version;
        this.user.location.id = data.locationId;
        this.user.department.id = data.departmentId;
        this.user.status = data.status;
        this.user.password = data.password;
        this.user.managerId = data.managerId;
        this.user.secContactNo = data.secContactNo;
        this.user.vendor = new Vendor();
        this.user.vendor.id = data.vendorId;
        //console.log(JSON.stringify(this.user));

        this.angForm.get('firstName').setValue(this.user.firstName);
        this.angForm.get('lastName').setValue(this.user.lastName);
        this.angForm.get('email').setValue(this.user.email);
        this.angForm.get('contactNo').setValue(this.user.contactNo);
        this.angForm.get('secContactNo').setValue(this.user.secContactNo);
        this.angForm.get('employeeId').setValue(this.user.employeeId);
        this.angForm.get('projectCode').setValue(this.user.projectCode);
        this.angForm.get('roles').setValue(this.user.roles.map(String));
        this.angForm.get('managerId').setValue(this.user.managerId);
        if (this.user.vendor.id != null) {
          this.angForm.get('vendorId').setValue(this.user.vendor.id);
          $(function () {
            $('#userType').val('Agent');
            $('.vendor-col').removeClass('d-none');
          });
        } else {
          $(function () {
            $('#userType').val('Employee');
            $('.vendor-col').addClass('d-none');
          });
        }
        if (!this.loggedInUserService.appManager()) {
          if (typeof this.departments !== 'undefined') {
            for (var i = 0; i < this.departments.length; i++) {
              if (this.departments[i].id == this.user.department.id) {
                this.angForm.get('department').setValue(this.user.department.id + '__' + this.departments[i].version);
              }
            }
          }
          if (typeof this.locations !== 'undefined') {
            for (var i = 0; i < this.locations.length; i++) {
              if (this.locations[i].id == this.user.location.id) {
                this.angForm.get('location').setValue(this.user.location.id + '__' + this.locations[i].version);
              }
            }
          }
        }
        this.statusFlag = String(this.user.status) == 'ACTIVE' ? true : false;
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        this.angForm.markAllAsTouched();
        this.preapareSelect();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }
}