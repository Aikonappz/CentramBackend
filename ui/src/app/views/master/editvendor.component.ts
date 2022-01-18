import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { AppUtility } from '../../config/AppUtility';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { Module } from '../../model/Module';
import { DistributionListModule } from '../../model/DistributionListModule';
import { Vendor } from '../../model/Vendor';
import { VendorModule } from '../../model/VendorModule';
import { Status } from '../../model/enumerator/Status';
import { TicketAllocationType } from '../../model/enumerator/TicketAllocationType';
declare var $: any;

@Component({
  selector: 'app-editvendor',
  templateUrl: './editvendor.component.html',
  styleUrls: ['./editvendor.component.scss']
})
export class EditVendorComponent implements OnInit {
  moduleName: string = "VENDOR";
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
  vendor: Vendor;
  vendorModules: VendorModule[];
  angForm: FormGroup;
  ticketAllocationTypes: any;
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
    this.vendor = new Vendor();
    this.vendor.status = this.defaultStatus;
    this.ticketAllocationTypes = Object.values(TicketAllocationType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));
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
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
      ]),
      ticketAllocationType: new FormControl('', [
        Validators.required,
      ]),
      status: new FormControl('ACTIVE', [
      ]),
      inHouse: new FormControl('1', [
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
          this.callVendorService(this.entityId);
        });
    }
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
      this.vendor.inHouse = (this.angForm.controls['inHouse'].value == true) ? true : false;
      this.vendor.name = this.angForm.controls['name'].value;
      this.vendor.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      this.submoduleIds = this.angForm.controls['dlSubModuleId'].value;
      this.vendor.ticketAllocationType = this.angForm.controls['ticketAllocationType'].value;
      this.vendorModules = [];
      for (let i in this.submoduleIds) {
        this.vendorModules[i] = new DistributionListModule(
          this.angForm.controls['dlModuleId'].value,
          this.submoduleIds[i]
        );
      }
      this.vendor.vendorModules = this.vendorModules;
      this.vendor.organisation = null;
      //console.log(this.user.status);
      //console.log(this.angForm.controls['status'].value);
      this.callSaveVendorService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveVendorService() {
    this.miscService
      .saveVendorService(this.vendor)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/master/vendor']);
      });
  }

  callVendorService(id: number) {
    this.miscService
      .vendorService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.vendor.id = data.id;
        this.vendor.name = data.name;
        this.vendor.status = data.status;
        this.vendor.version = data.version;
        this.vendor.vendorModules = data.vendorModules;
        this.vendor.ticketAllocationType = data.ticketAllocationType;
        this.vendor.inHouse = data.inHouse;
        //console.log(JSON.stringify(this.user));

        this.submoduleIds = [];
        let moduleId = null;
        for (let k in this.vendor.vendorModules) {
          this.submoduleIds.push(this.vendor.vendorModules[k].subModuleId);
          moduleId = this.vendor.vendorModules[k].moduleId;
        }
        this.populateSubmodule(moduleId);
        this.angForm.get('inHouse').setValue(this.vendor.inHouse);
        this.angForm.get('name').setValue(this.vendor.name);
        this.angForm.get('dlModuleId').setValue(moduleId);
        this.angForm.get('dlSubModuleId').setValue(this.submoduleIds.map(Number));
        this.angForm.get('ticketAllocationType').setValue(this.vendor.ticketAllocationType);
        this.statusFlag = String(this.vendor.status) == 'ACTIVE' ? true : false;
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

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }
}