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
import { LoggedInUserService } from '../../service/LoggedInUserService';

@Component({
  selector: 'app-editdepartment',
  templateUrl: './editdepartment.component.html',
  styleUrls: ['./editdepartment.component.scss']
})
export class EditDepartmentComponent implements OnInit {
  moduleName: string = "DEPARTMENT";
  actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  dept: Department;
  angForm = new FormGroup({
    name: new FormControl('', [
      Validators.required,
    ]),
    status: new FormControl(true, [
    ]),
  });
  constructor(
    private loggedInUserService: LoggedInUserService,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private miscService: MiscService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.dept = new Department();
    this.dept.status = this.defaultStatus;
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

    } else {
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.callGetUserService(this.entityId);
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.dept.name = this.angForm.controls['name'].value;
      this.dept.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user.status);
      //console.log(this.angForm.controls['status'].value);
      this.callSaveDepartmentService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveDepartmentService() {
    this.miscService
      .saveDepartmentService(this.dept)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/master/department']);
      });
  }

  callGetUserService(id: number) {
    this.miscService
      .departmentService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.dept.id = data.id;
        this.dept.name = data.name;
        this.dept.status = data.status;
        this.dept.version = data.version;
        //console.log(JSON.stringify(this.user));

        this.angForm.get('name').setValue(this.dept.name);
        this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    //this.statusFlag = this.active_status.nativeElement.checked;
    //console.log(status);
    //console.log(inp);
    this.statusFlag = status;
  }
}