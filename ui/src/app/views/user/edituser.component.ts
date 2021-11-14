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
import { AppUtility } from '../../config/AppUtility';

@Component({
  selector: 'app-edituser',
  templateUrl: './edituser.component.html',
  styleUrls: ['./edituser.component.scss']
})
export class EditUserComponent implements OnInit {
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  user: User;
  roles: Role[];
  locations: LocationVO[];
  departments: Department[];
  angForm = new FormGroup({
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
    employeeId: new FormControl('NA', [
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
    status: new FormControl(true, [

    ]),
  });
  constructor(
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private miscService: MiscService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.user = new User();
    this.user.status = this.defaultStatus;
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
          let c = 0;
          for (let i = 0; i < this.roles.length; i++) {
            if (AppUtility.appManager()) {
              if (!this.roles[i].name.match(/ORG_.*/)) {
                tmpRoles[c] = this.roles[i];
                c++;
              }
            } else {
              if (this.roles[i].name.match(/ORG_.*/)) {
                tmpRoles[c] = this.roles[i];
                c++;
              }
            }
          }
          this.roles = tmpRoles;
        });
      this.miscService
        .locationsService()
        .subscribe((data: any) => {
          //console.log("load locations");
          this.locations = data.content;
        });
      this.miscService
        .departmentsService()
        .subscribe((data: any) => {
          //console.log("load departments");
          this.departments = data.content;
        });
    } else {
      this.miscService
        .rolesService()
        .subscribe((data: any) => {
          //console.log("load roles");
          this.roles = data.content;
          let tmpRoles: any = [];
          let c = 0;
          for (let i = 0; i < this.roles.length; i++) {
            if (AppUtility.appManager()) {
              if (!this.roles[i].name.match(/ORG_.*/)) {
                tmpRoles[c] = this.roles[i];
                c++;
              }
            } else {
              if (this.roles[i].name.match(/ORG_.*/)) {
                tmpRoles[c] = this.roles[i];
                c++;
              }
            }
          }
          this.roles = tmpRoles;
          this.miscService
            .locationsService()
            .subscribe((data: any) => {
              //console.log("load locations");
              this.locations = data.content;
              this.miscService
                .departmentsService()
                .subscribe((data: any) => {
                  //console.log("load departments");
                  this.departments = data.content;
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
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.user.firstName = this.angForm.controls['firstName'].value;
      this.user.lastName = this.angForm.controls['lastName'].value;
      this.user.email = this.angForm.controls['email'].value;
      this.user.contactNo = this.angForm.controls['contactNo'].value;
      this.user.employeeId = this.angForm.controls['employeeId'].value;
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
      /* process department and location */
      this.user.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user.status);
      if (this.newEntity) {
        this.callAddUserService();
      } else {
        this.callEditUserService();
      }
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callAddUserService() {
    this.userService
      .addUserService(this.user)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/user']);
      });
  }

  callEditUserService() {
    this.userService
      .editUserService(this.user)
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
        this.user.projectCode = data.projectCode;
        this.user.roles = data.roles;
        this.user.organisation.id = data.organisationId;
        this.user.version = data.version;
        this.user.location.id = data.locationId;
        this.user.department.id = data.departmentId;
        this.user.status = data.status;
        this.user.password = data.password;
        //console.log(JSON.stringify(this.user));

        this.angForm.get('firstName').setValue(this.user.firstName);
        this.angForm.get('lastName').setValue(this.user.lastName);
        this.angForm.get('email').setValue(this.user.email);
        this.angForm.get('contactNo').setValue(this.user.contactNo);
        this.angForm.get('employeeId').setValue(this.user.employeeId);
        this.angForm.get('projectCode').setValue(this.user.projectCode);
        this.angForm.get('roles').setValue(this.user.roles.map(String));
        for (var i = 0; i < this.departments.length; i++) {
          if (this.departments[i].id == this.user.department.id) {
            this.angForm.get('department').setValue(this.user.department.id + '__' + this.departments[i].version);
          }
        }
        for (var i = 0; i < this.locations.length; i++) {
          if (this.locations[i].id == this.user.location.id) {
            this.angForm.get('location').setValue(this.user.location.id + '__' + this.locations[i].version);
          }
        }
        this.statusFlag = String(this.user.status) == 'ACTIVE' ? true : false;
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  isChecked() {
    this.statusFlag = this.status.nativeElement.checked;
    //console.log("Check status = >" + this.status.nativeElement.checked);
  }
}