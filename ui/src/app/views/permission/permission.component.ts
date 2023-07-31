import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService, ModalOptions, } from 'ngx-bootstrap/modal';
import { AppUtility } from '../../config/AppUtility';
import { Action } from '../../model/Action';
import { Module } from '../../model/Module';
import { PermissionDTO } from '../../model/PermissionDTO';
import { Role } from '../../model/Role';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { ViewPermissableAction } from './model/ViewPermissableAction';
import { ViewTaggedModal } from './model/ViewTaggedModule';
declare var $: any;

@Component({
  selector: 'app-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.scss']
})
export class PermissionComponent implements OnInit, OnDestroy {
  moduleName: string = "PERMISSION";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  angForm: FormGroup;
  drStatus: { isOpen: boolean } = { isOpen: false };
  disabled: boolean = false;
  isDropup: boolean = true;
  autoClose: boolean = false;
  modalRef: BsModalRef;
  permissionDTO: PermissionDTO;

  roles: Role[] = [];
  modules: Module[];
  taggedModules: Module[];
  actions: Action[];
  permisableActions: Action[];
  dataSaved: boolean = false;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: MiscService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      role: new FormControl(null, [
        Validators.required,
      ]),
      module: new FormControl(null, [
        Validators.required,
      ]),
      action: new FormControl(null, [
        Validators.required,
      ]),
    });

    //role 
    this.service
      .rolesService()
      .subscribe((data: any) => {
        this.roles = data.content;
      });
    this.service
      .modulesService()
      .subscribe((data: any) => {
        this.modules = [];
        let d = null;
        for (let k in data.content) {
          //if (data.content[k].appModule) {
          d = data.content[k];
          if (d.organisation == null) {
            if (d.parentModuleId != null) {
              if (d.licenseType == "INCIDENT") {
                d.name = "INCIDENT/" + d.parentModuleName + "/" + d.name;
              } else if (d.licenseType == "ASSET") {
                d.name = "ASSET/" + d.parentModuleName + "/" + d.name;
              } else {
                d.name = d.parentModuleName + "/" + d.name;
              }
            }
            this.modules.push(d);
          }
          //}
        }
      });
    this.service
      .actionsService()
      .subscribe((data: any) => {
        this.actions = data.content;
      });
  }

  @ViewChild("module") module;
  triggerModuleChange(module) {
    if (typeof module !== 'undefined') {
      let mdl = module.id;
      let rl = this.angForm.controls['role'].value;
      if (rl != null && mdl != null) {
        // this.service
        //   .getActionsByRoleAndModule(rl, mdl, {})
        //   .subscribe((data: any) => {
        //     this.permisableActions = data;
        //     //$('.view-action').removeClass("d-none");
        //     //console.log(data);
        //     if (this.permisableActions.length > 0) {
        //       let actions = [];
        //       for (let k in this.permisableActions) {
        //         actions.push(this.permisableActions[k].id);
        //         //console.log(actions);
        //       }
        //       this.angForm.get('action').setValue(actions.map(Number));
        //     } else {
        //       this.angForm.controls['action'].setValue(null);
        //       $('.view-action').addClass("d-none");
        //     }
        //   });
      } else {
        this.angForm.controls['action'].setValue(null);
        $('.view-action').addClass("d-none");
      }
    } else {
      this.angForm.controls['action'].setValue(null);
      $('.view-action').addClass("d-none");
    }
  }

  @ViewChild("role") role;
  changeRole(role) {
    if (typeof role !== 'undefined') {
      this.service
        .getModulesByRole(role.id, {})
        .subscribe((data: any) => {
          this.taggedModules = [];
          for (let k in data) {
            if (data[k].appModule) {
              this.taggedModules.push(data[k]);
            }
          }
        });
      $('.view-module').removeClass("d-none");
      this.angForm.controls['module'].setValue(null);
    } else {
      $('.view-module').addClass("d-none");
      this.angForm.controls['module'].setValue(null);
    }
  }

  onHidden(): void {
    //console.log('Dropdown is hidden');
  }
  onShown(): void {
    //console.log('Dropdown is shown');
  }
  isOpenChange(): void {
    //console.log('Dropdown state is changed');
  }
  toggleDropdown($event: MouseEvent): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.drStatus.isOpen = !this.drStatus.isOpen;
  }
  change(value: boolean): void {
    this.drStatus.isOpen = value;
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
  }
  ngOnDestroy() {
    this.drStatus.isOpen = false;
  }
  ngAfterViewInit() {
  }
  loadData(req = {}) {
  }

  loadPage() {
    this.angForm.reset();
    this.loadData({});
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }
  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      this.permissionDTO = new PermissionDTO();
      this.permissionDTO.moduleIds = this.angForm.controls['module'].value;
      this.permissionDTO.roleId = this.angForm.controls['role'].value;
      this.permissionDTO.actionIds = this.angForm.controls['action'].value;
      //console.log(this.permissionDTO);
      this.service
        .permissionService(this.permissionDTO)
        .subscribe((data: any) => {
          this.dataSaved = true;
          this.angForm.reset();
        });
    } else {
      console.log("Invalid Form!");
    }
  }

  viewModules() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      taggedModules: this.taggedModules
    };
    this.modalRef = this.modalService.show(ViewTaggedModal, Object.assign({}, config, { initialState }));
  }

  viewActions() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      permisableActions: this.permisableActions
    };
    this.modalRef = this.modalService.show(ViewPermissableAction, Object.assign({}, config, { initialState }));
  }

}
