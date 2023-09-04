import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../../service/MiscService';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { Status } from '../../../model/enumerator/Status';
import { UserService } from '../../../service/UserService';
import { UserVO, UserVOListResponse } from '../../../model/UserVO';
import { Project } from '../../../model/Project';
import { ProjectType } from '../../../model/enumerator/ProjectTyps';
import { Technology } from '../../../model/enumerator/Technology';
import { StartEndDateValidation } from '../../../validator/StartEndDateValidation';
import { AppUtility } from '../../../config/AppUtility';
import * as moment from 'moment';
declare var $: any;

@Component({
  selector: 'app-editproject',
  templateUrl: './editproject.component.html',
  styleUrls: ['./editproject.component.scss']
})
export class EditProjectComponent implements OnInit {
  moduleName: string = "PROJECT_MASTER";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  angForm: FormGroup;
  type: string;
  users: UserVO[] = [];
  managers: UserVO[] = [];
  uatStakeHolders: UserVO[] = [];
  uatConsultants: UserVO[] = [];
  projectTypes: any[] = [];
  project: Project;
  alphaNumericRegex = /^[a-z0-9]+$/i;
  projectModules: any[] = [];
  moduleList: any[] = [];
  subModuleList: any[] = [];
  technologyList: any[] = [];
  constructor(
    private fb: FormBuilder,
    private loggedInUserService: LoggedInUserService,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private miscService: MiscService,
    private userService: UserService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.project = new Project();
    this.project.status = this.defaultStatus;
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
    });
    //this.type = this.route.snapshot.paramMap.get('licenceType');
    for (let item in Technology) {
      if (item != "ALL") {
        this.technologyList.push({ "key": item, "value": Technology[item] });
      }
    }
    for (let item in ProjectType) {
      if (item != "ALL" && item != "HOURLY" && item != "FULL_BID") {
        this.projectTypes.push({ "key": item, "value": ProjectType[item] });
      }
    }
    if (!this.route.snapshot.paramMap.has('id')) {
      this.miscService
        .modulesService({ licenseType: 'UAT' })
        .subscribe((data: any) => {
          //console.log(JSON.stringify(data));
          this.projectModules = [];
          for (let k = 0; k < data.content.length; k++) {
            if (data.content[k].projectModule)
              //console.log(data.content[k].technology);
              this.projectModules.push(data.content[k]);
          }
          this.moduleList = [];
          this.subModuleList = [];
          // for (let k = 0; k < this.projectModules.length; k++) {
          //   if (this.projectModules[k].parentModuleId == null)
          //     this.moduleList.push(this.projectModules[k]);
          // }
        });
      this.userService.getUsersService()
        .subscribe((result: UserVOListResponse) => {
          //let logedinUser = this.loggedInUserService.getLoggedInUser();
          this.users = [];
          this.managers = [];
          this.uatConsultants = [];
          this.uatStakeHolders = [];
          for (let i = 0; i < result.content.length; i++) {
            this.users.push(result.content[i]);
          }
          for (let i = 0; i < this.users.length; i++) {
            if (this.users[i].roleNames.includes('ORG_ADMIN_PROJECT')) {
              this.managers.push(this.users[i]);
            }
            if (this.users[i].roleNames.includes('ORG_UAT_CONSULTANT')) {
              this.uatConsultants.push(this.users[i]);
            }
            if (this.users[i].roleNames.includes('ORG_PROJECT_STAKEHOLDER')) {
              this.uatStakeHolders.push(this.users[i]);
            }
          }
        });
    } else {
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.newEntity = false;
      this.miscService
        .modulesService({ licenseType: 'UAT' })
        .subscribe((data: any) => {
          //console.log(JSON.stringify(data));
          this.projectModules = [];
          for (let k = 0; k < data.content.length; k++) {
            if (data.content[k].projectModule)
              //console.log(data.content[k].technology);
              this.projectModules.push(data.content[k]);
          }
          this.moduleList = [];
          this.subModuleList = [];
          for (let k = 0; k < this.projectModules.length; k++) {
            if (this.projectModules[k].parentModuleId == null)
              this.moduleList.push(this.projectModules[k]);
          }
          this.userService.getUsersService()
            .subscribe((result: UserVOListResponse) => {
              //let logedinUser = this.loggedInUserService.getLoggedInUser();
              this.users = [];
              this.managers = [];
              this.uatConsultants = [];
              this.uatStakeHolders = [];
              for (let i = 0; i < result.content.length; i++) {
                this.users.push(result.content[i]);
              }
              for (let i = 0; i < this.users.length; i++) {
                if (this.users[i].roleNames.includes('ORG_ADMIN_PROJECT')) {
                  this.managers.push(this.users[i]);
                }
                if (this.users[i].roleNames.includes('ORG_UAT_CONSULTANT')) {
                  this.uatConsultants.push(this.users[i]);
                }
                if (this.users[i].roleNames.includes('ORG_PROJECT_STAKEHOLDER')) {
                  this.uatStakeHolders.push(this.users[i]);
                }
              }
              this.callVendorService(this.entityId);
            });
        });
    }


    this.angForm = this.fb.group({
      name: new FormControl(null, [
        Validators.required,
        Validators.maxLength(255),
      ]),
      code: new FormControl(null, [
        Validators.required,
        Validators.maxLength(10),
        Validators.pattern(this.alphaNumericRegex),
      ]),
      projectType: new FormControl(null, [
        Validators.required,
      ]),
      watchList: new FormControl(null, [
        Validators.required,
      ]),
      technology: new FormControl(null, [
        Validators.required,
      ]),
      moduleId: new FormControl(null, [
        Validators.required,
      ]),
      subModuleId: new FormControl(null, [
        Validators.required,
      ]),
      consultants: new FormControl(null, [
        Validators.required,
      ]),
      stakeHolders: new FormControl(null, [
        Validators.required,
      ]),
      status: new FormControl('ACTIVE', [
      ]),
      inHouse: new FormControl('1', [
      ]),
      start: new FormControl('', [
        Validators.required
      ]),
      end: new FormControl('', [
        Validators.required
      ]),
    }, {
      validators: StartEndDateValidation('start', 'end')
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
      this.project.inHouse = false;
      this.project.name = this.angForm.controls['name'].value;
      this.project.code = this.angForm.controls['code'].value;
      this.project.projectType = Object.keys(ProjectType).indexOf(this.angForm.controls['projectType'].value);
      //ProjectType[this.angForm.controls['projectType'].value];
      this.project.watchList = this.angForm.controls['watchList'].value;
      this.project.stakeHolders = this.angForm.controls['stakeHolders'].value;
      this.project.consultants = this.angForm.controls['consultants'].value;
      this.project.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      this.project.moduleId = this.angForm.controls['moduleId'].value;
      this.project.subModuleId = this.angForm.controls['subModuleId'].value;
      this.project.technology = Object.keys(Technology).indexOf(this.angForm.controls['technology'].value);
      //Technology[this.angForm.controls['technology'].value];
      this.project.start = AppUtility.prepareDateToString(moment(this.angForm.controls['start'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      this.project.end = AppUtility.prepareDateToString(moment(this.angForm.controls['end'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      //console.log(this.vendor);
      //console.log(this.angForm.controls['status'].value);
      //console.log(JSON.stringify(this.project));
      this.callSaveProjectService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveProjectService() {
    this.miscService
      .saveProjectService(this.project)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/admin/master/project']);
      });
  }

  callVendorService(id: number) {
    this.miscService
      .projectService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.project.id = data.id;
        this.project.name = data.name;
        this.project.code = data.code;
        this.project.status = data.status;
        this.project.version = data.version;
        this.project.watchList = data.watchList;
        this.project.stakeHolders = data.stakeHolders;
        this.project.consultants = data.consultants;
        this.project.projectType = data.projectType;
        this.project.inHouse = data.inHouse;
        //console.log(JSON.stringify(this.user));
        //this.populateSubmodule(moduleId);
        this.angForm.get('projectType').setValue(this.project.projectType);
        this.angForm.get('inHouse').setValue(this.project.inHouse);
        this.angForm.get('name').setValue(this.project.name);
        this.angForm.get('code').setValue(this.project.code);
        this.statusFlag = String(this.project.status) == 'ACTIVE' ? true : false;
        this.angForm.get('watchList').setValue(this.project.watchList.map(String));
        this.angForm.get('stakeHolders').setValue(this.project.stakeHolders.map(String));
        this.angForm.get('consultants').setValue(this.project.consultants.map(String));
        //this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    //console.log(moduleId);
    if (typeof moduleId !== 'undefined') {
      this.subModuleList = [];
      for (let i = 0; i < this.projectModules.length; i++) {
        if (this.projectModules[i].projectModule == true && this.projectModules[i].parentModuleId == moduleId.id) {
          this.subModuleList.push(this.projectModules[i]);
        }
      }
    } else {
      this.subModuleList = [];
      this.angForm.get('subModuleId').setValue(null);
    }
  }

  @ViewChild("technology") technology;
  populateModule(technology) {
    //console.log(technology);
    if (typeof technology !== 'undefined') {
      this.moduleList = [];
      for (let i = 0; i < this.projectModules.length; i++) {
        if (this.projectModules[i].projectModule == true && this.projectModules[i].technology == technology.value) {
          this.moduleList.push(this.projectModules[i]);
        }
      }
    } else {
      this.moduleList = [];
      this.angForm.get('moduleId').setValue(null);
      this.subModuleList = [];
      this.angForm.get('subModuleId').setValue(null);
    }
  }
}