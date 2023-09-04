import { Component, OnInit, ViewChild, } from '@angular/core';

import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';

import { BsModalRef, BsModalService, ModalOptions, } from 'ngx-bootstrap/modal';

import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';

import { MatPaginator } from '@angular/material/paginator';

import * as moment from 'moment';

import { SelectionModel } from '@angular/cdk/collections';
import { ProjectUat } from '../../../model/ProjectUat';
import { ProjectUatScript } from '../../../model/ProjectUatScript';
import { ProjectUatScriptDetailSource } from '../../../service/datasource/ProjectUatScriptDetailSource';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { AssetOrderService } from '../../../service/AssetOrderService';
import { MiscService } from '../../../service/MiscService';
import { UserService } from '../../../service/UserService';
import { MediaService } from '../../../service/MediaService';
import { ClientStorageService } from '../../../service/ClientStorageService';
import { ProjectUatService } from '../../../service/ProjectUatService';
import { ProjectList } from '../../../model/Project';
import { ProjectUatScriptDataSource } from '../../../service/datasource/ProjectUatScriptDataSource';
import { AppUtility } from '../../../config/AppUtility';
import { ProjectUatScriptDetail } from '../../../model/ProjectUatScriptDetail';
import { RemarkViewer } from '../modal/RemarkViewer';
import { MediaType } from '../../../model/enumerator/MediaType';
import { EntityType } from '../../../model/enumerator/EntityType';
import { Technology } from '../../../model/enumerator/Technology';
import { ProjectUatDataSource } from '../../../service/datasource/ProjectUatDataSource';
declare var $: any;

@Component({
  selector: 'app-uat-activity',
  templateUrl: './uat-activity.component.html',
  styleUrls: ['./uat-activity.component.scss']
})
export class UATActivityComponent implements OnInit {
  moduleName: string = "UAT ACTIVITIES";
  angForm: FormGroup;
  uatCommunicationSearchForm: FormGroup;
  angUatCycleSearchForm: FormGroup;
  allProjectList: any[];
  projectList: any[];
  projectModules: any[];
  technologyList: any[] = [];
  moduleList: any[];
  subModuleList: any[];
  searchSubModuleList: any[];
  selectedUatScriptFiles?: FileList;
  uatScriptFiles?: File[] = [];
  selectedUatManualFiles?: FileList;
  uatManualFiles?: File[] = [];
  projectUat: ProjectUat;
  uploadSuccess: boolean = false;
  projectUatScriptList: ProjectUatScript[];
  projectUatScript: ProjectUatScript = null;
  projectUats: ProjectUat[];

  uatCommunicationSearchedParam: Object = {};
  searched: boolean = false;
  searchedUatCycle: boolean = false;
  searchedUatCycleId: number;
  statusList: any[] = [{ id: true, label: "Pass" }, { id: false, label: "Fail" }];
  loggedInUser: LoggedInUser;
  modalRef: BsModalRef;
  searchedUatScriptId: number;
  searchedUatScriptComplete: boolean = false;

  projectUatDisplayedColumns = ['technology', 'module', 'subModule', 'project', 'uatScript', 'uatManual'];
  private projectUatDataSource: ProjectUatDataSource;
  @ViewChild('projectUatPaginator') projectUatPaginator: MatPaginator;

  projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'retestDetail', 'remarks', 'activity'];
  private projectUatScriptDetailSource: ProjectUatScriptDetailSource;
  @ViewChild('projectUatScriptDetailPaginator') projectUatScriptDetailPaginator: MatPaginator;

  //uatCycleDisplayedColumns = ['select', 'testCaseId', 'testScriptName', 'testCaseDescription', 'testScenario', 'plannedDate'];
  uatCycleDisplayedColumns = ['testCaseId', 'testScriptName', 'testCaseDescription', 'testScenario', 'plannedDate'];
  datasourceUatCycle: ProjectUatScriptDataSource;
  @ViewChild('MatPaginator2') uatCyclePaginator: MatPaginator;
  selection = new SelectionModel<ProjectUatScript>(true, []);

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
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
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
        this.allProjectList = this.projectList;
      });
    for (let item in Technology) {
      if (item != "ALL") {
        this.technologyList.push({ "key": item, "value": Technology[item] });
      }
    }
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
      });
    if (this.isConsultant()) {
      this.angForm = this.fb.group({
        technology: new FormControl(null, [
          Validators.required,
        ]),
        moduleId: new FormControl(null, [
          Validators.required,
        ]),
        subModuleId: new FormControl(null, [
          Validators.required,
        ]),
        project: new FormControl(null, [
          Validators.required,
        ]),
        uatScript: new FormControl('', [
        ]),
        uatManual: new FormControl('', [
        ]),
      });
    }
    if (this.isConsultant() || this.isCustomer()) {
      this.uatCommunicationSearchForm = this.fb.group({
        searchTechnology: new FormControl(null, [
          Validators.required,
        ]),
        searchModuleId: new FormControl(null, [
          Validators.required,
        ]),
        searchSubModuleId: new FormControl(null, [
          Validators.required,
        ]),
        searchProject: new FormControl(null, [
          Validators.required,
        ]),
        searchUatProjectId: new FormControl(null, [
          Validators.required,
        ]),
        searchUatProjectScriptId: new FormControl(null, [
          Validators.required,
        ]),
      });
    }
    if (this.isCustomer()) {
      this.angUatCycleSearchForm = this.fb.group({
        searchUatCycleProject: new FormControl(null, [
          Validators.required,
        ]),
        searchUatCycleModuleId: new FormControl(null, [
          Validators.required,
        ]),
        searchUatCycleSubModuleId: new FormControl(null, [
          Validators.required,
        ]),
        searchUatCycleProjectId: new FormControl(null, [
          Validators.required,
        ]),
      });
    }
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
    this.projectUatScriptDetailSource = new ProjectUatScriptDetailSource(this.projectUatService);
    this.projectUatScriptDetailSource.load(0, 10, this.uatCommunicationSearchedParam);

    this.datasourceUatCycle = new ProjectUatScriptDataSource(this.projectUatService);
    this.datasourceUatCycle.load(0, 10, this.uatCommunicationSearchedParam);

    if (this.isConsultant()) {
      this.projectUatDataSource = new ProjectUatDataSource(this.projectUatService);
      this.projectUatDataSource.load(0, 10, {});
      this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'remarks', 'activity'];
    }
  }

  ngAfterViewInit() {
    this.projectUatScriptDetailSource.counter$
      .pipe(
        tap((count) => {
          this.projectUatScriptDetailPaginator.length = count;
        })
      )
      .subscribe();
    this.projectUatScriptDetailPaginator.page
      .pipe(
        tap(() => this.loadData())
      )
      .subscribe();
    if (this.isCustomer()) {
      this.datasourceUatCycle.counter$
        .pipe(
          tap((count) => {
            this.uatCyclePaginator.length = count;
          })
        )
        .subscribe();
      this.uatCyclePaginator.page
        .pipe(
          tap(() => this.uatCycleloadData())
        )
        .subscribe();
    }
    if (this.isConsultant()) {
      this.projectUatDataSource.counter$
        .pipe(
          tap((count) => {
            this.projectUatPaginator.length = count;
          })
        )
        .subscribe();
      this.projectUatPaginator.page
        .pipe(
          tap(() => this.projectUatData())
        )
        .subscribe();
    }
  }

  ngAfterContentInit() {
  }



  get f() { return this.angForm.controls; }

  get sf() { return this.uatCommunicationSearchForm.controls; }

  get suf() { return this.angUatCycleSearchForm.controls; }

  /**
   * 
   */
  uploadUatScript() {
    if (this.angForm.valid) {
      let projectUATRequestDTO = {
        technology: Object.keys(Technology).indexOf(this.angForm.controls['technology'].value),
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




  searchUatCycleFormSubmit() {
    if (this.angUatCycleSearchForm.valid) {
      //console.log(this.angUatCycleSearchForm.controls['searchUatCycleProjectId'].value);
      this.uatCommunicationSearchedParam = { "projectUatId": this.angUatCycleSearchForm.controls['searchUatCycleProjectId'].value };
      this.uatCycleloadData();
      this.searchedUatCycle = true;
      this.searchedUatCycleId = this.angUatCycleSearchForm.controls['searchUatCycleProjectId'].value;
      //console.log(this.isUatCycleComplete());
    } else {
      console.log("Invalid Form!");
    }
  }

  isUatCycleComplete() {
    for (let k = 0; k < this.projectUats.length; k++) {
      if (this.projectUats[k].id == this.searchedUatCycleId && this.projectUats[k].uatCycleComplete) {
        return true;
      }
    }
    return false;
  }

  markUatCycleComplete() {
    let res = window.confirm("Are you sure?")
    if (res) {
      this.projectUatService
        .markUATCycleComplete(this.searchedUatCycleId)
        .subscribe((data: ProjectUat) => {
          //console.log("updated data", JSON.stringify(data));
          this.searchedUatCycle = false;
          this.angUatCycleSearchForm.reset();
        });
    }
  }

  /**
   * 
   * @param req 
   */
  projectUatData(req = {}) {
    this.projectUatDataSource.load(this.projectUatScriptDetailPaginator.pageIndex, this.projectUatScriptDetailPaginator.pageSize, req);
  }

  uatCycleloadData(req = {}) {
    this.datasourceUatCycle.load(this.projectUatScriptDetailPaginator.pageIndex, this.projectUatScriptDetailPaginator.pageSize, this.uatCommunicationSearchedParam);
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.datasourceUatCycle.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.datasourceUatCycle.data.forEach(row => {
        //console.log(row);
        this.selection.select(row);
      });
  }

  logSelection() {
    this.selection.selected.forEach(s => console.log(s.id));
  }

  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
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
    this.uatCommunicationSearchForm.reset();
  }

  resetSearchUatCycleForm() {
    this.searchedUatCycle = false;
    this.angUatCycleSearchForm.reset();
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
            .saveMediaService(data.id, EntityType.PROJECT_UAT, MediaType.PROJECT_UAT_MANUAL, "NA", formData, { 'headers': headers })
            .subscribe((data: any) => {
              //console.log(data);
              this.angForm.reset();
              this.uploadSuccess = true;
              this.projectUatData();
            });
        }
      });
  }







  /**
   * 
   */
  @ViewChild("searchUatCycleProject") searchUatCycleProject;
  uatCycleProjectModifyAction(searchUatCycleProject) {
    //console.log(moduleId);
    this.searchSubModuleList = [];
    this.projectUatScriptList = [];
    this.projectUats = [];
    this.angUatCycleSearchForm.get('searchUatCycleModuleId').setValue(null);
    this.angUatCycleSearchForm.get('searchUatCycleSubModuleId').setValue(null);
    this.angUatCycleSearchForm.get('searchUatCycleProjectId').setValue(null);
  }

  /**
  * 
  */
  @ViewChild("searchUatCycleModuleId") searchUatCycleModuleId;
  uatCyclePopulateSubmodule(searchUatCycleModuleId) {
    //console.log(moduleId);
    if (typeof searchUatCycleModuleId !== 'undefined') {
      this.searchSubModuleList = [];
      for (let i = 0; i < this.projectModules.length; i++) {
        if (this.projectModules[i].projectModule == true && this.projectModules[i].parentModuleId == searchUatCycleModuleId.id) {
          this.searchSubModuleList.push(this.projectModules[i]);
        }
      }
      this.projectUatScriptList = [];
      this.projectUats = [];
      this.angUatCycleSearchForm.get('searchUatCycleSubModuleId').setValue(null);
      this.angUatCycleSearchForm.get('searchUatCycleProjectId').setValue(null);
    } else {
      this.searchSubModuleList = [];
      this.projectUatScriptList = [];
      this.projectUats = [];
      this.angUatCycleSearchForm.get('searchUatCycleSubModuleId').setValue(null);
      this.angUatCycleSearchForm.get('searchUatCycleProjectId').setValue(null);
    }
  }

  @ViewChild("searchUatCycleSubModuleId") searchUatCycleSubModuleId;
  uatCyclePopulateProjectUAT(searchUatCycleSubModuleId) {
    //console.log(moduleId);
    if (typeof searchUatCycleSubModuleId !== 'undefined') {
      // console.log(
      //   this.angSearchForm.controls['searchProject'].value,
      //   this.angSearchForm.controls['searchModuleId'].value,
      //   this.angSearchForm.controls['searchSubModuleId'].value
      // );
      if (
        this.angUatCycleSearchForm.controls['searchUatCycleModuleId'].value != null && this.angUatCycleSearchForm.controls['searchUatCycleModuleId'].value != "" &&
        this.angUatCycleSearchForm.controls['searchUatCycleSubModuleId'].value != null && this.angUatCycleSearchForm.controls['searchUatCycleSubModuleId'].value != "" &&
        this.angUatCycleSearchForm.controls['searchUatCycleProject'].value != null && this.angUatCycleSearchForm.controls['searchUatCycleProject'].value != ""
      ) {
        this.projectUatService
          .getProjectUats({
            projectId: this.angUatCycleSearchForm.controls['searchUatCycleProject'].value,
            moduleId: this.angUatCycleSearchForm.controls['searchUatCycleModuleId'].value,
            subModuleId: this.angUatCycleSearchForm.controls['searchUatCycleSubModuleId'].value,
          })
          .subscribe((data: ProjectUat[]) => {
            this.projectUats = data;
            for (let k = 0; k < this.projectUats.length; k++) {
              if (this.projectUats[k].uatCycleComplete == false) {
                this.projectUats[k].label = this.projectUats[k].uatCycleName;
              } else {
                this.projectUats[k].label = this.projectUats[k].uatCycleName + " - Complete";
              }
            }
            this.projectUatScriptList = [];
            this.angUatCycleSearchForm.get('searchUatCycleProjectId').setValue(null);
          });
      }
    } else {
      this.projectUats = [];
      this.projectUatScriptList = [];
      this.angUatCycleSearchForm.get('searchUatCycleProjectId').setValue(null);
    }
  }








  /**
   * 
   * @param projectUatScriptDetail 
   */
  enableEdit(projectUatScriptDetail: ProjectUatScriptDetail) {
    projectUatScriptDetail.editable = true
    this.clientStorageService.set(projectUatScriptDetail.id.toString(), JSON.stringify(projectUatScriptDetail));
  }

  /**
   * 
   * @param projectUatScriptDetail 
   */
  saveElement(projectUatScriptDetail: ProjectUatScriptDetail) {
    if (projectUatScriptDetail.remark != null) {
      if (projectUatScriptDetail.remarks == null) {
        projectUatScriptDetail.remarks = [];
      }
      projectUatScriptDetail.remarks.push(
        { name: this.loggedInUser.name, email: this.loggedInUser.email, comment: projectUatScriptDetail.remark, }
      );
    }
    if (projectUatScriptDetail.actualResult != null && projectUatScriptDetail.pass == false && projectUatScriptDetail.retestDate != null && projectUatScriptDetail.retestDate != "") {
      projectUatScriptDetail.retestDate = moment(projectUatScriptDetail.retestDate).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
    }
    //console.log(this.clientStorageService.get(projectUatScriptDetail.id.toString()));
    //console.log(JSON.stringify(projectUatScriptDetail));
    //console.log((this.clientStorageService.get(projectUatScriptDetail.id.toString())) === JSON.stringify(projectUatScriptDetail));    
    if (!(this.clientStorageService.get(projectUatScriptDetail.id.toString()) === JSON.stringify(projectUatScriptDetail))) {
      this.projectUatService
        .saveProjectUatScriptDetail(projectUatScriptDetail)
        .subscribe((data: any) => {
          this.clientStorageService.remove(projectUatScriptDetail.id.toString());
          projectUatScriptDetail.remark = null;
          projectUatScriptDetail.editable = false;
          //console.log("updated data", JSON.stringify(data));
        });
    } else {
      projectUatScriptDetail.editable = false;
    }
  }

  /**
   * 
   * @param projectUatScriptDetail 
   */
  viewRemark(projectUatScriptDetail: ProjectUatScriptDetail) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-lg',
    };
    const initialState = {
      projectUatScriptDetail: projectUatScriptDetail,
    };
    this.modalRef = this.modalService.show(RemarkViewer,
      Object.assign({}, config, { initialState })
    );
  }

  /**
   * marking ProjectUat script as complete
   */
  markProjectUatScriptTestComplate() {
    let res = window.confirm("Are you sure?")
    if (res) {
      this.projectUatService
        .markProjectUatScriptComplete(this.searchedUatScriptId)
        .subscribe((data: ProjectUatScript) => {
          //console.log("updated data", JSON.stringify(data));
          this.searched = false;
          this.uatCommunicationSearchForm.reset();
        });
    }
  }

  /**
   * 
   */
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
      this.angForm.get('moduleId').setValue(null);
      this.subModuleList = [];
      this.angForm.get('subModuleId').setValue(null);
      this.projectList = [];
      this.angForm.get('project').setValue(null);
    } else {
      this.moduleList = [];
      this.angForm.get('moduleId').setValue(null);
      this.subModuleList = [];
      this.angForm.get('subModuleId').setValue(null);
      this.projectList = [];
      this.angForm.get('project').setValue(null);
    }
  }

  /**
   * 
   */
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
      this.angForm.get('subModuleId').setValue(null);
      this.projectList = [];
      this.angForm.get('project').setValue(null);
    } else {
      this.subModuleList = [];
      this.angForm.get('subModuleId').setValue(null);
      this.projectList = [];
      this.angForm.get('project').setValue(null);
    }
  }

  /**
   * 
   */
  @ViewChild("subModuleId") subModuleId;
  populateProject(subModuleId) {
    if (typeof subModuleId !== 'undefined') {
      this.projectList = [];
      //console.log(Technology[this.angForm.controls['technology'].value], this.angForm.controls['moduleId'].value, this.angForm.controls['subModuleId'].value);
      for (let k = 0; k < this.allProjectList.length; k++) {
        if (Technology[this.angForm.controls['technology'].value] == this.allProjectList[k].technology && this.angForm.controls['moduleId'].value == this.allProjectList[k].moduleId && this.angForm.controls['subModuleId'].value == this.allProjectList[k].subModuleId) {
          this.projectList.push(this.allProjectList[k]);
        }
      }
      this.angForm.get('project').setValue(null);
    } else {
      this.projectList = [];
      this.angForm.get('project').setValue(null);
    }
  }

  /**
   * 
   * @param idFile 
   * @param fileName 
   * @returns 
   */
  downloadFile(idFile: number, fileName: string) {
    this.mediaService
      .downloadMediaService(idFile, {})
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: data.type });
        const file = new File([blob], fileName, { type: data.type });
        let url = window.URL.createObjectURL(file);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          //alert('Please disable your Pop-up blocker and try again.');
        }
      });
    return false;
  }

  /**
  * 
  */
  @ViewChild("searchTechnology") searchTechnology;
  populateSearchModule(searchTechnology) {
    //console.log(technology);
    if (typeof searchTechnology !== 'undefined') {
      this.moduleList = [];
      for (let i = 0; i < this.projectModules.length; i++) {
        if (this.projectModules[i].projectModule == true && this.projectModules[i].technology == searchTechnology.value) {
          this.moduleList.push(this.projectModules[i]);
        }
      }
    } else {
      this.moduleList = [];
    }
    this.uatCommunicationSearchForm.get('searchModuleId').setValue(null);
    this.searchSubModuleList = [];
    this.uatCommunicationSearchForm.get('searchSubModuleId').setValue(null);
    this.projectList = [];
    this.uatCommunicationSearchForm.get('searchProject').setValue(null);
    this.projectUats = [];
    this.uatCommunicationSearchForm.get('searchUatProjectId').setValue(null);
    this.projectUatScriptList = [];
    this.uatCommunicationSearchForm.get('searchUatProjectScriptId').setValue(null);
    this.projectUatScript = null;
    this.searched = false;
    // refresh uatCommunication table
    // this.uatCommunicationSearchFormSubmit();
  }

  /**
   * 
   */
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
    }
    this.uatCommunicationSearchForm.get('searchSubModuleId').setValue(null);
    this.projectList = [];
    this.uatCommunicationSearchForm.get('searchProject').setValue(null);
    this.projectUats = [];
    this.uatCommunicationSearchForm.get('searchUatProjectId').setValue(null);
    this.projectUatScriptList = [];
    this.uatCommunicationSearchForm.get('searchUatProjectScriptId').setValue(null);
    // refresh uatCommunication table
    // this.uatCommunicationSearchFormSubmit();
    this.projectUatScript = null;
    this.searched = false;
  }

  /**
  * 
  */
  @ViewChild("searchSubModuleId") searchSubModuleId;
  populateSearchProject(searchSubModuleId) {
    if (typeof searchSubModuleId !== 'undefined') {
      this.projectList = [];
      //console.log(Technology[this.angForm.controls['technology'].value], this.angForm.controls['moduleId'].value, this.angForm.controls['subModuleId'].value);
      for (let k = 0; k < this.allProjectList.length; k++) {
        if (Technology[this.uatCommunicationSearchForm.controls['searchTechnology'].value] == this.allProjectList[k].technology
          && this.uatCommunicationSearchForm.controls['searchModuleId'].value == this.allProjectList[k].moduleId
          && this.uatCommunicationSearchForm.controls['searchSubModuleId'].value == this.allProjectList[k].subModuleId) {
          this.projectList.push(this.allProjectList[k]);
        }
      }
    } else {
      this.projectList = [];
    }
    this.uatCommunicationSearchForm.get('searchProject').setValue(null);
    this.projectUats = [];
    this.uatCommunicationSearchForm.get('searchUatProjectId').setValue(null);
    this.projectUatScriptList = [];
    this.uatCommunicationSearchForm.get('searchUatProjectScriptId').setValue(null);
    // refresh uatCommunication table
    // this.uatCommunicationSearchFormSubmit();
    this.projectUatScript = null;
    this.searched = false;
  }

  /**
  * 
  */
  @ViewChild("searchProject") searchProject;
  populateProjectUat(searchProject) {
    if (typeof searchProject !== 'undefined') {
      // console.log(
      //   this.angSearchForm.controls['searchProject'].value,
      //   this.angSearchForm.controls['searchModuleId'].value,
      //   this.angSearchForm.controls['searchSubModuleId'].value
      // );
      if (
        this.uatCommunicationSearchForm.controls['searchSubModuleId'].value != null && this.uatCommunicationSearchForm.controls['searchSubModuleId'].value != "" &&
        this.uatCommunicationSearchForm.controls['searchModuleId'].value != null && this.uatCommunicationSearchForm.controls['searchModuleId'].value != "" &&
        this.uatCommunicationSearchForm.controls['searchProject'].value != null && this.uatCommunicationSearchForm.controls['searchProject'].value != ""
      ) {
        this.projectUatService
          .getProjectUats({
            projectId: this.uatCommunicationSearchForm.controls['searchProject'].value,
            moduleId: this.uatCommunicationSearchForm.controls['searchModuleId'].value,
            subModuleId: this.uatCommunicationSearchForm.controls['searchSubModuleId'].value,
          })
          .subscribe((data: ProjectUat[]) => {
            this.projectUats = data;
            for (let k = 0; k < this.projectUats.length; k++) {
              if (this.projectUats[k].uatCycleComplete == false) {
                this.projectUats[k].label = this.projectUats[k].uatCycleName;
              } else {
                this.projectUats[k].label = this.projectUats[k].uatCycleName + " - Complete";
              }
            }
          });
      } else {
        this.projectUats = [];
      }
    } else {
      this.projectUats = [];
    }
    this.uatCommunicationSearchForm.get('searchUatProjectId').setValue(null);
    this.projectUatScriptList = [];
    this.uatCommunicationSearchForm.get('searchUatProjectScriptId').setValue(null);
    // refresh uatCommunication table
    // this.uatCommunicationSearchFormSubmit();
    this.projectUatScript = null;
    this.searched = false;
  }

  /**
   * 
   */
  @ViewChild("searchUatProjectId") searchUatProjectId;
  populateProjectUATScript(searchUatProjectId) {
    //console.log(moduleId);
    if (typeof searchUatProjectId !== 'undefined') {
      this.projectUatService
        .getProjectUatScripts({
          uatProjectId: this.uatCommunicationSearchForm.controls['searchUatProjectId'].value,
        })
        .subscribe((data: ProjectUatScript[]) => {
          this.projectUatScriptList = data;
          for (let k = 0; k < this.projectUatScriptList.length; k++) {
            if (this.projectUatScriptList[k].uatComplete == false) {
              this.projectUatScriptList[k].label = this.projectUatScriptList[k].testCaseId;
            } else {
              this.projectUatScriptList[k].label = this.projectUatScriptList[k].testCaseId + " - Complete";
            }
          }
        });
    } else {
      this.projectUatScriptList = [];
    }
    this.uatCommunicationSearchForm.get('searchUatProjectScriptId').setValue(null);
    // refresh uatCommunication table
    // this.uatCommunicationSearchFormSubmit();
    this.projectUatScript = null;
    this.searched = false;
  }

  /**
  * 
  */
  @ViewChild("searchUatProjectScriptId") searchUatProjectScriptId;
  searchUatCommunication(searchUatProjectScriptId) {
    if (typeof searchUatProjectScriptId !== 'undefined') {
      for (let i = 0; i < this.projectUatScriptList.length; i++) {
        if (searchUatProjectScriptId.id == this.projectUatScriptList[i].id) {
          this.searched = true;
          this.projectUatScript = this.projectUatScriptList[i];
        }
      }
      this.uatCommunicationSearchFormSubmit();
    } else {
      this.projectUatScript = null;
      this.searched = false;
    }
  }

  /**
   * 
   */
  loadData() {
    this.projectUatScriptDetailSource.load(this.projectUatScriptDetailPaginator.pageIndex, this.projectUatScriptDetailPaginator.pageSize, this.uatCommunicationSearchedParam);
  }

  /**
   * 
   */
  uatCommunicationSearchFormSubmit() {
    /*if (this.uatCommunicationSearchForm.valid) {
      this.searchedUatScriptId = this.uatCommunicationSearchForm.controls['searchUatProjectScriptId'].value;
      this.uatCommunicationSearchedParam = {
        projectUATScriptId: this.uatCommunicationSearchForm.controls['searchUatProjectScriptId'].value,
      };
      //console.log(this.isScriptUATComplete());
      if (this.isScriptUATComplete()) {
        this.searchedUatScriptComplete = true;
        this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'retestDetail', 'remarks',];
      } else {
        this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'retestDetail', 'remarks', 'activity'];
      }
      this.loadData();
      this.searched = true;
    } else {
      console.log("Invalid Form!");
    }*/
    this.searchedUatScriptId = this.uatCommunicationSearchForm.controls['searchUatProjectScriptId'].value;
    this.uatCommunicationSearchedParam = { projectUATScriptId: this.uatCommunicationSearchForm.controls['searchUatProjectScriptId'].value, };
    if (this.isScriptUATComplete()) {
      this.searchedUatScriptComplete = true;
      if (this.isConsultant()) {
        this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'remarks',];
      } else {
        this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'retestDetail', 'remarks',];
      }
    } else {
      if (this.isConsultant()) {
        this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'remarks', 'activity'];
      } else {
        this.projectUatScriptDetailDisplayedColumns = ['uatDescription', 'actualResultDetail', 'retestDetail', 'remarks', 'activity'];
      }
    }
    this.loadData();
    this.searched = true;
  }

  /**
  * 
  * @param id 
  * @returns 
  */
  isScriptUATComplete() {
    for (let k = 0; k < this.projectUatScriptList.length; k++) {
      if (this.projectUatScriptList[k].id == this.searchedUatScriptId && this.projectUatScriptList[k].uatComplete == true) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @returns 
   */
  isConsultant() {
    return this.hasRoles(['ORG_UAT_CONSULTANT']);
  }

  /**
   * 
   * @returns 
   */
  isCustomer() {
    return this.hasRoles(['ORG_PROJECT_STAKEHOLDER']);
  }

  /**
   * 
   * @param element 
   * @returns 
   */
  retestNotEditable(element: ProjectUatScriptDetail): boolean {
    if (!element.editable || element.pass) {
      return true;
    }
    return false;
  }

}