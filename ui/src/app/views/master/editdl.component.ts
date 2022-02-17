import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { AppUtility } from '../../config/AppUtility';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { Module } from '../../model/Module';
import { DistributionList } from '../../model/DistributionList';
import { DistributionListModule } from '../../model/DistributionListModule';
declare var $: any;

@Component({
  selector: 'app-editdl',
  templateUrl: './editdl.component.html',
  styleUrls: ['./editdl.component.scss']
})
export class EditDlComponent implements OnInit {
  moduleName: string = "DEPARTMENT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  modules: Module[] = [];
  moduleList: Module[] = [];
  subModuleList: Module[];
  submoduleIds: number[];
  statusFlag: boolean = true;
  entityId: number;
  dl: DistributionList;
  dlms: DistributionListModule[];
  angForm: FormGroup;
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
    this.dl = new DistributionList();
    //this.dept.status = this.defaultStatus;
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
    this.angForm = this.fb.group({
      dlModuleId: new FormControl(true, [
        Validators.required,
      ]),
      dlSubModuleId: new FormControl(true, [
        Validators.required,
      ]),
      dlName: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
      ]),
      dlEmail: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        Validators.email,
      ]),
    });
    if (!this.route.snapshot.paramMap.has('id')) {
      this.miscService
        .modulesService()
        .subscribe((data: any) => {
          this.modules = data.content;
          for (let i in this.modules) {
            if (this.modules[i].appModule == false && this.modules[i].parentModuleId == null) {
              this.modules[i].customerModuleName = AppUtility.toTitleCase(this.modules[i].customerModuleName);
              this.moduleList.push(this.modules[i]);
            }
          }
        });
    } else {
      this.miscService
        .modulesService()
        .subscribe((data: any) => {
          this.modules = data.content;
          for (let i in this.modules) {
            if (this.modules[i].appModule == false && this.modules[i].parentModuleId == null) {
              this.modules[i].customerModuleName = AppUtility.toTitleCase(this.modules[i].customerModuleName);
              this.moduleList.push(this.modules[i]);
            }
          }
          this.newEntity = false;
          this.entityId = Number(this.route.snapshot.paramMap.get('id'));
          this.callDistributionListService(this.entityId);
        });
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.dl.dlName = this.angForm.controls['dlName'].value;
      this.dl.dlEmail = this.angForm.controls['dlEmail'].value;
      this.submoduleIds = this.angForm.controls['dlSubModuleId'].value;
      this.dlms = [];
      for (let i in this.submoduleIds) {
        this.dlms[i] = new DistributionListModule(
          this.angForm.controls['dlModuleId'].value,
          this.submoduleIds[i]
        );
      }
      this.dl.distributionListModules = this.dlms;
      this.dl.organisation = null;
      //console.log(this.user.status);
      //console.log(this.angForm.controls['status'].value);
      this.callSaveDistributionListService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveDistributionListService() {
    this.miscService
      .saveDistributionListService(this.dl)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/master/dl']);
      });
  }

  callDistributionListService(id: number) {
    this.miscService
      .distributionListService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.dl.id = data.id;
        this.dl.dlName = data.dlName;
        this.dl.dlEmail = data.dlEmail;
        this.dl.version = data.version;
        this.dl.distributionListModules = data.distributionListModules;
        //console.log(JSON.stringify(this.user));

        this.submoduleIds = [];
        let moduleId = null;
        for (let k in this.dl.distributionListModules) {
          this.submoduleIds.push(this.dl.distributionListModules[k].subModuleId);
          moduleId = this.dl.distributionListModules[k].moduleId;
        }
        this.populateSubmodule(moduleId);
        this.angForm.get('dlEmail').setValue(this.dl.dlEmail);
        this.angForm.get('dlName').setValue(this.dl.dlName);
        this.angForm.get('dlModuleId').setValue(moduleId);
        this.angForm.get('dlSubModuleId').setValue(this.submoduleIds.map(String));
        this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("dlModuleId") dlModuleId;
  populateSubmodule(mId) {
    let c = 0;
    if (mId != "") {
      this.subModuleList = [];
      for (let i = 0; i < this.modules.length; i++) {
        if (this.modules[i].parentModuleId == mId) {
          this.modules[i].customerModuleName = AppUtility.toTitleCase(this.modules[i].customerModuleName);
          this.subModuleList.push(this.modules[i]);
          c++;
        }
      }
    }
  }
}