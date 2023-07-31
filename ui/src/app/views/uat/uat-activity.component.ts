import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MediaService } from '../../service/MediaService';
import { ClientStorageService } from '../../service/ClientStorageService';
import { AssetOrderService } from '../../service/AssetOrderService';
import { BsModalService, } from 'ngx-bootstrap/modal';
import { Project, ProjectList } from '../../model/Project';
import { ProjectUat } from '../../model/ProjectUat';
import { ProjectUatService } from '../../service/ProjectUatService';
import { EntityType } from '../../model/enumerator/EntityType';
import { MediaType } from '../../model/enumerator/MediaType';
import { ProjectUatScript } from '../../model/ProjectUatScript';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ProjectUatScriptDetailSource } from '../../service/datasource/ProjectUatScriptDetailSource';
import { MatPaginator } from '@angular/material/paginator';
import { ProjectUatScriptDetail } from '../../model/ProjectUatScriptDetail';
declare var $: any;

@Component({
  selector: 'app-uat-activity',
  templateUrl: './uat-activity.component.html',
  styleUrls: ['./uat-activity.component.scss']
})
export class UATActivityComponent implements OnInit {
  moduleName: string = "UAT ACTIVITIES";
  angForm: FormGroup;
  angSearchForm: FormGroup;
  projectList: any[];
  projectModules: any[];
  moduleList: any[];
  subModuleList: any[];
  searchSubModuleList: any[];
  selectedUatScriptFiles?: FileList;
  uatScriptFiles?: File[] = [];
  selectedUatManualFiles?: FileList;
  uatManualFiles?: File[] = [];
  projectUat: ProjectUat;
  uploadSuccess: boolean = false;
  projectUatScripts: ProjectUatScript[];
  displayedColumns = ['uatDescription', 'actualResultDetail', 'retestDetail', 'remarks', 'activity'];
  //displayedColumns = ['testScenarioJobId', 'step','action', 'activity'];
  private datasource: ProjectUatScriptDetailSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  searchedData: Object = {};
  searched: boolean = false;
  statusList: any[] = [{ id: true, label: "Pass" }, { id: false, label: "Fail" }];

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
    private projectUatService: ProjectUatService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.projectUat = new ProjectUat();
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
    this.angSearchForm = this.fb.group({
      searchProject: new FormControl(null, [
        Validators.required,
      ]),
      searchModuleId: new FormControl(null, [
        Validators.required,
      ]),
      searchSubModuleId: new FormControl(null, [
        Validators.required,
      ]),
      searchScriptId: new FormControl(null, [
        Validators.required,
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
    this.datasource = new ProjectUatScriptDetailSource(this.projectUatService);
    this.datasource.load(0, 10, this.searchedData);
  }

  ngAfterViewInit() {
    this.datasource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();
    this.paginator.page
      .pipe(
        tap(() => this.loadData())
      )
      .subscribe();
  }

  ngAfterContentInit() {
  }

  loadData() {
    this.datasource.load(this.paginator.pageIndex, this.paginator.pageSize, this.searchedData);
  }

  get f() { return this.angForm.controls; }

  get sf() { return this.angSearchForm.controls; }

  /**
   * 
   */
  formSubmit() {
    if (this.angForm.valid) {
      let projectUATRequestDTO = {
        projectId: this.angForm.controls['project'].value,
        moduleId: this.angForm.controls['moduleId'].value,
        subModuleId: this.angForm.controls['subModuleId'].value
      };
      const file: File | null = this.selectedUatScriptFiles.item(0);
      const formData: FormData = new FormData();
      formData.append('file', file, file.name);
      formData.append('projectUATRequestDTO', new Blob([JSON.stringify(projectUATRequestDTO)], { type: "application/json" }));
      let headers = new Headers();
      headers.append('Content-Type', 'multipart/form-data');
      headers.set('Accept', 'application/json');
      this.callUploadProjectUatScript(formData);
    } else {
      console.log("Invalid Form!");
    }
  }

  searchFormSubmit() {
    if (this.angSearchForm.valid) {
      this.searchedData = {
        projectId: this.angSearchForm.controls['searchProject'].value,
        moduleId: this.angSearchForm.controls['searchModuleId'].value,
        subModuleId: this.angSearchForm.controls['searchSubModuleId'].value,
        projectUATScriptId: this.angSearchForm.controls['searchScriptId'].value,
      };
      this.loadData();
      this.searched = true;
    } else {
      console.log("Invalid Form!");
    }
  }

  /**
   * 
   */
  resetUploadForm() {
    this.angForm.reset();
  }

  /**
   * 
   */
  resetSearchForm() {
    this.searched = false;
    this.angSearchForm.reset();
  }

  /**
   * upload and save script file and manual altogether
   * @param formData 
   */
  callUploadProjectUatScript(formData: FormData) {
    this.projectUatService
      .uploadProjectUatScript(formData)
      .subscribe((data: any) => {
        //console.log(data);
        if (data != null && data.id != null) {
          const formData: FormData = new FormData();
          for (var i = 0; i < this.selectedUatManualFiles.length; i++) {
            formData.append("file", this.selectedUatManualFiles[i]);
          }
          let headers = new Headers();
          headers.append('Content-Type', 'multipart/form-data');
          headers.set('Accept', 'application/json');
          this.mediaService
            .saveMediaService(data.id, EntityType.PROJECT_UAT, MediaType.INCIDENT_COMMUNICATION, "NA", formData, { 'headers': headers })
            .subscribe((data: any) => {
              //console.log(data);
              this.angForm.reset();
              this.uploadSuccess = true;
            });
        }
      });
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

  @ViewChild("searchModuleId") searchModuleId;
  populateSearchSubmodule(searchModuleId) {
    //console.log(moduleId);
    if (typeof searchModuleId !== 'undefined') {
      this.searchSubModuleList = [];
      for (let i = 0; i < this.projectModules.length; i++) {
        if (this.projectModules[i].projectModule == true && this.projectModules[i].parentModuleId == searchModuleId.id) {
          this.searchSubModuleList.push(this.projectModules[i]);
        }
      }
    } else {
      this.searchSubModuleList = [];
      this.angSearchForm.get('searchSubModuleId').setValue(null);
      this.angSearchForm.get('searchScriptId').setValue(null);
    }
  }

  @ViewChild("searchSubModuleId") searchSubModuleId;
  populateProjectUATScript(searchSubModuleId) {
    //console.log(moduleId);
    if (typeof searchSubModuleId !== 'undefined') {
      // console.log(
      //   this.angSearchForm.controls['searchProject'].value,
      //   this.angSearchForm.controls['searchModuleId'].value,
      //   this.angSearchForm.controls['searchSubModuleId'].value
      // );
      this.projectUatService
        .getProjectUatScripts({
          projectId: this.angSearchForm.controls['searchProject'].value,
          moduleId: this.angSearchForm.controls['searchModuleId'].value,
          subModuleId: this.angSearchForm.controls['searchSubModuleId'].value,
        })
        .subscribe((data: any) => {
          this.projectUatScripts = data;
          for (let k = 0; k < this.projectUatScripts.length; k++) {
            this.projectUatScripts[k].label = this.projectUatScripts[k].testScriptName + " [" + this.projectUatScripts[k].plannedDate + "]";
          }
        });
    } else {
      this.projectUatScripts = [];
      this.angSearchForm.get('searchScriptId').setValue(null);
    }

  }

  saveElement(projectUatScriptDetail: ProjectUatScriptDetail) {
    projectUatScriptDetail.editable = false;
    console.log(JSON.stringify(projectUatScriptDetail));
  }

}