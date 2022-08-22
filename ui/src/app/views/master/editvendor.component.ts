import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { AppUtility } from '../../config/AppUtility';
import { LoggedInUserService } from '../../service/LoggedInUserService';
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
  modules: any[] = [];
  moduleList: any[] = [];
  subModuleList: any[] = [];
  submoduleIds: number[];
  statusFlag: boolean = true;
  entityId: number;
  vendor: Vendor;
  vendorModules: VendorModule[] = [];
  angForm: FormGroup;
  ticketAllocationTypes: any[] = [];
  type: string;
  hasAllocationType: boolean = true;
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
      this.type = this.route.snapshot.paramMap.get('licenceType');
      if (!this.route.snapshot.paramMap.has('id')) {
        this.miscService
          .modulesService({ licenseType: this.type.toUpperCase() })
          .subscribe((data: any) => {
            //console.log(JSON.stringify(data.content));
            this.moduleList = [];
            this.modules = data.content;
            for (let i in this.modules) {
              if (this.modules[i].appModule == false && this.modules[i].parentModuleId == null && this.modules[i].licenseType != null && this.modules[i].licenseType == this.type.toUpperCase()) {
                this.modules[i].customerModuleName = AppUtility.toTitleCase(this.modules[i].customerModuleName);
                this.moduleList.push(this.modules[i]);
              }
            }
          });
      } else {
        this.miscService
          .modulesService({ licenseType: this.type.toUpperCase() })
          .subscribe((data: any) => {
            this.modules = data.content;
            this.moduleList = [];
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
      if (this.type == "asset") {
        this.hasAllocationType = false;
        this.angForm = this.fb.group({
          dlModuleId: new FormControl(null, [
            Validators.required,
          ]),
          dlSubModuleId: new FormControl(null, [
            Validators.required,
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
          contactNumber: new FormControl(null, [
            Validators.required,
            Validators.pattern(this.phoneRegex),
          ]),
          contactAddress: new FormControl(null, [
            Validators.required,
          ]),
          ticketAllocationType: new FormControl(null, [
            //Validators.required,
          ]),
          status: new FormControl('ACTIVE', [
          ]),
          inHouse: new FormControl('1', [
          ]),
        });
      } else {
        this.hasAllocationType = true;
        this.angForm = this.fb.group({
          dlModuleId: new FormControl(null, [
            Validators.required,
          ]),
          dlSubModuleId: new FormControl(null, [
            Validators.required,
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
          inHouse: new FormControl('1', [
          ]),
        });
      }
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
      this.vendor.inHouse = (this.angForm.controls['inHouse'].value == true) ? true : false;
      this.vendor.name = this.angForm.controls['name'].value;
      this.vendor.contactAddress = this.angForm.controls['contactAddress'].value;
      this.vendor.contactEmail = this.angForm.controls['contactEmail'].value;
      this.vendor.contactName = this.angForm.controls['contactName'].value;
      this.vendor.contactNumber = this.angForm.controls['contactNumber'].value;
      this.vendor.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      this.submoduleIds = this.angForm.controls['dlSubModuleId'].value;
      this.vendor.ticketAllocationType = this.angForm.controls['ticketAllocationType'].value == "" ? null : this.angForm.controls['ticketAllocationType'].value;
      this.vendor.vendorType = this.type.toUpperCase() == "ASSET" ? 0 : 1;
      this.vendorModules = [];
      let c = 0;
      for (let i in this.submoduleIds) {
        //console.log(this.submoduleIds[i]);
        for (let k in this.subModuleList) {
          if (this.subModuleList[k].id == this.submoduleIds[i]) {
            this.vendorModules[i] = new VendorModule(
              this.subModuleList[k].parentModuleId,
              this.subModuleList[k].id
            );
            c++;
          }
        }
      }
      this.vendor.vendorModules = this.vendorModules;
      this.vendor.organisation = null;
      //console.log(this.vendor);
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
        this.router.navigate(['/master/vendor/' + this.type]);
      });
  }

  callVendorService(id: number) {
    this.miscService
      .vendorService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.vendor.id = data.id;
        this.vendor.name = data.name;
        this.vendor.contactAddress = data.contactAddress;
        this.vendor.contactEmail = data.contactEmail;
        this.vendor.contactName = data.contactName;
        this.vendor.contactNumber = data.contactNumber;
        this.vendor.status = data.status;
        this.vendor.version = data.version;
        this.vendor.vendorModules = data.vendorModules;
        this.vendor.ticketAllocationType = data.ticketAllocationType;
        this.vendor.inHouse = data.inHouse;
        //console.log(JSON.stringify(this.user));
        let parentModules = [];
        for (let i in this.vendor.vendorModules) {
          parentModules[i] = { id: this.vendor.vendorModules[i].moduleId };
        }
        this.submoduleIds = [];
        let moduleIds = [];
        for (let k in this.vendor.vendorModules) {
          this.submoduleIds.push(this.vendor.vendorModules[k].subModuleId);
          moduleIds.push(this.vendor.vendorModules[k].moduleId);
        }
        this.angForm.get('dlModuleId').setValue(moduleIds.map(Number));
        this.populateSubmodule(parentModules);
        //this.populateSubmodule(moduleId);
        this.angForm.get('inHouse').setValue(this.vendor.inHouse);
        this.angForm.get('name').setValue(this.vendor.name);
        this.angForm.get('contactAddress').setValue(this.vendor.contactAddress);
        this.angForm.get('contactEmail').setValue(this.vendor.contactEmail);
        this.angForm.get('contactName').setValue(this.vendor.contactName);
        this.angForm.get('contactNumber').setValue(this.vendor.contactNumber);
        this.angForm.get('dlSubModuleId').setValue(this.submoduleIds.map(Number));
        this.angForm.get('dlSubModuleId').markAsTouched();
        this.angForm.get('ticketAllocationType').setValue(this.vendor.ticketAllocationType);
        this.statusFlag = String(this.vendor.status) == 'ACTIVE' ? true : false;
        //this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("dlModuleId") dlModuleId;
  populateSubmodule(mId) {
    //console.log(mId);
    if (typeof mId !== 'undefined') {
      let c = 0;
      let moduleIds = [];
      for (let i = 0; i < mId.length; i++) {
        moduleIds.push(mId[i].id);
      }
      this.subModuleList = [];
      for (let i = 0; i < this.modules.length; i++) {
        if (moduleIds.length > 0 && moduleIds.includes(this.modules[i].parentModuleId)) {
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