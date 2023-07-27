import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MediaService } from '../../service/MediaService';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { ClientStorageService } from '../../service/ClientStorageService';
import { AssetOrder } from '../../model/AssetOrder';
import { AssetOrderService } from '../../service/AssetOrderService';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { CommonAlert } from '../../containers/default-layout/modal/CommonAlert';
import { UserVO, UserVOListResponse } from '../../model/UserVO';
import { Project, ProjectList } from '../../model/Project';
import { ProjectAllocationDetail } from '../../model/ProjectAllocationDetail';
import { StartEndDateValidation } from '../../validator/StartEndDateValidation';
declare var $: any;

@Component({
  selector: 'app-uat-activity',
  templateUrl: './uat-activity.component.html',
  styleUrls: ['./uat-activity.component.scss']
})
export class UATActivityComponent implements OnInit {
  moduleName: string = "UAT ACTIVITIES";
  angForm: FormGroup;
  projectList: any[];
  projectModules: any[];
  moduleList: any[];
  subModuleList: any[];
  selectedUatScriptFiles?: FileList;
  uatScriptFiles?: File[] = [];
  selectedUatManualFiles?: FileList;
  uatManualFiles?: File[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
    private assetOrderService: AssetOrderService,
    private miscService: MiscService,
    private userService: UserService,
    private mediaService: MediaService,
    private clientStorageService: ClientStorageService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });

    this.miscService.projectsService()
      .subscribe((result: ProjectList) => {
        this.projectList = [];
        for (let k = 0; k < result.content.length; k++) {
          this.projectList.push(result.content[k]);
        }
        for (let k = 0; k < this.projectList.length; k++) {
          this.projectList[k].label = this.projectList[k].name + " [" + this.projectList[k].code + "]";
        }
      });


    this.miscService
      .modulesService({ licenseType: 'UAT' })
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.projectModules = [];
        for (let k = 0; k < data.content.length; k++) {
          if (data.content[k].projectModule)
            this.projectModules.push(data.content[k]);
        }
        this.moduleList = [];
        this.subModuleList = [];
        for (let k = 0; k < this.projectModules.length; k++) {
          if (this.projectModules[k].parentModuleId == null)
            this.moduleList.push(this.projectModules[k]);
        }
      });

    this.miscService.projectsService()
      .subscribe((result: ProjectList) => {
        this.projectList = result.content;
      });
    this.angForm = this.fb.group({
      project: new FormControl(null, [
        Validators.required,
      ]),
      moduleId: new FormControl(null, [
        Validators.required,
      ]),
      subModuleId: new FormControl(null, [
        Validators.required,
      ]),
      uatScript: new FormControl('', [
      ]),
      uatManual: new FormControl('', [
      ]),
    });

  }

  getUatScriptFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      this.selectedUatScriptFiles = null;
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angForm.controls['uatScript'];
      if (file.errors && !file.errors.validAttachments && !file.errors.mustBeLessThan2MB) {
        return;
      }
      let validMimeTpes = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"];
      if (!validMimeTpes.includes(type)) {
        file.setErrors({ validAttachments: true, mustBeLessThan2MB: false });
      } else if (size > (3145728)) {
        file.setErrors({ validAttachments: false, mustBeLessThan2MB: true });
      } else {
        file.setErrors(null);
        this.selectedUatScriptFiles = event.target.files;
        for (var i = 0; i < this.selectedUatScriptFiles.length; i++) {
          this.uatScriptFiles.push(this.selectedUatScriptFiles[i]);
        }
        //console.log(this.allSelectedFiles);
      }
      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");
    }
  }

  getUatManualFileDetails(ev) {
    for (var i = 0; i < ev.target.files.length; i++) {
      this.selectedUatManualFiles = null;
      var name = ev.target.files[i].name;
      var type = ev.target.files[i].type;
      var size = ev.target.files[i].size;
      var modifiedDate = ev.target.files[i].lastModifiedDate;
      const file = this.angForm.controls['uatManual'];
      if (file.errors && !file.errors.validAttachments && !file.errors.mustBeLessThan2MB) {
        return;
      }
      let validMimeTpes = ["application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf",];
      if (!validMimeTpes.includes(type)) {
        file.setErrors({ validAttachments: true, mustBeLessThan2MB: false });
      } else if (size > (3145728)) {
        file.setErrors({ validAttachments: false, mustBeLessThan2MB: true });
      } else {
        file.setErrors(null);
        this.selectedUatManualFiles = ev.target.files;
        for (var i = 0; i < this.selectedUatManualFiles.length; i++) {
          this.uatManualFiles.push(this.selectedUatManualFiles[i]);
        }
        //console.log(this.allSelectedFiles);
      }
      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");
    }
  }

  hasRoles(roles: string[]) {
    let userRoles = this.loggedInUserService.getRoles();
    for (let k = 0; k < userRoles.length; k++) {
      if (roles.includes(userRoles[k])) {
        return true;
      }
    }
    return false;
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
      //console.log(this.route.snapshot.paramMap.get('referer'));
    });

  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {

    } else {
      console.log("Invalid Form!");
    }
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
    }
  }

}