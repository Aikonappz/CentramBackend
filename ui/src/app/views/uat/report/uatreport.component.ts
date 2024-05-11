import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../../config/AppUtility';
import { ReportAssetOrderDataSource } from '../../../service/datasource/ReportAssetOrderDataSource';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { MiscService } from '../../../service/MiscService';
import { ReportService } from '../../../service/ReportService';
import { UserService } from '../../../service/UserService';
import { ProjectList } from '../../../model/Project';
import { Technology } from '../../../model/enumerator/Technology';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { ProjectUat } from '../../../model/ProjectUat';
import { ProjectUatScript } from '../../../model/ProjectUatScript';
import { ProjectUatService } from '../../../service/ProjectUatService';
import { UatScriptReportDataSource } from '../../../service/datasource/UatScriptReportDataSource';

@Component({
  selector: 'app-uat-report',
  templateUrl: './uatreport.component.html',
  styleUrls: ['./uatreport.component.scss']
})
export class UATReportComponent implements OnInit {
  moduleName: string = "UAT REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  //displayedColumns = ['projDtl', 'consltDtl', 'testCaseDtl', 'status',];
  displayedColumns = ['project', 'technology', 'module', 'subModule', 'actionDetails', 'status',];
  datasource: UatScriptReportDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: any[] = [];
  uatCommunicationSearchForm: FormGroup;
  searchedData: any = {};


  allProjectList: any[];
  projectList: any[];
  projectModules: any[];
  technologyList: any[] = [];
  moduleList: any[];
  subModuleList: any[];
  searchSubModuleList: any[];
  loggedInUser: LoggedInUser;
  projectUat: ProjectUat;
  uploadSuccess: boolean = false;
  projectUatScriptList: ProjectUatScript[];
  projectUatScript: ProjectUatScript = null;
  projectUats: ProjectUat[];

  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: ReportService,
    private userService: UserService,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private route: ActivatedRoute,
    private projectUatService: ProjectUatService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.miscService.projectsService()
      .subscribe((result: ProjectList) => {
        this.projectList = [];
        if (this.isConsultant()) {
          for (let k = 0; k < result.content.length; k++) {
            if (result.content[k].consultants.includes(this.loggedInUser.email))
              this.projectList.push(result.content[k]);
          }
          for (let k = 0; k < this.projectList.length; k++) {
            this.projectList[k].label = this.projectList[k].name + " [" + this.projectList[k].code + "]";
          }
        } else if (this.isCustomer()) {
          for (let k = 0; k < result.content.length; k++) {
            if (result.content[k].stakeHolders.includes(this.loggedInUser.email))
              this.projectList.push(result.content[k]);
          }
          for (let k = 0; k < this.projectList.length; k++) {
            this.projectList[k].label = this.projectList[k].name + " [" + this.projectList[k].code + "]";
          }
        } else if (this.isProjectAdmin()) {
          for (let k = 0; k < result.content.length; k++) {
            if (result.content[k].watchList.includes(this.loggedInUser.email))
              this.projectList.push(result.content[k]);
          }
          for (let k = 0; k < this.projectList.length; k++) {
            this.projectList[k].label = this.projectList[k].name + " [" + this.projectList[k].code + "]";
          }
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

    this.statusList.push({ id: 'PENDING', label: 'Pending Approval' });
    this.statusList.push({ id: 'PARTIALLY_APPROVED', label: '1st Level Approved' });
    this.statusList.push({ id: 'APPROVED', label: '2nd Level Approved' });
    this.uatCommunicationSearchForm = this.fb.group({
      searchTechnology: new FormControl(null, [

      ]),
      searchModuleId: new FormControl(null, [

      ]),
      searchSubModuleId: new FormControl(null, [

      ]),
      searchProject: new FormControl(null, [

      ]),
      searchUatProjectId: new FormControl(null, [

      ]),
      searchUatProjectScriptId: new FormControl(null, [

      ]),
    });
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
      this.moduleList.sort((a, b) => (a.customerModuleName < b.customerModuleName ? -1 : 1));
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
      this.searchSubModuleList.sort((a, b) => (a.customerModuleName < b.customerModuleName ? -1 : 1));
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
  }

  /**
  * 
  */
  @ViewChild("searchUatProjectScriptId") searchUatProjectScriptId;
  searchUatCommunication(searchUatProjectScriptId) {
    if (typeof searchUatProjectScriptId !== 'undefined') {
      for (let i = 0; i < this.projectUatScriptList.length; i++) {
        if (searchUatProjectScriptId.id == this.projectUatScriptList[i].id) {
          this.projectUatScript = this.projectUatScriptList[i];
        }
      }
      //this.uatCommunicationSearchFormSubmit();
    } else {
      this.projectUatScript = null;
    }
  }

  /**
   * 
   * @param roles 
   * @returns 
   */
  hasRoles(roles: string[]) {
    let userRoles = this.loggedInUserService.getRoles();
    for (let k = 0; k < userRoles.length; k++) {
      if (roles.includes(userRoles[k])) {
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
   * @returns 
   */
  isProjectAdmin() {
    return this.hasRoles(['ORG_ADMIN_PROJECT']);
  }

  /**
   * 
   * @param action 
   * @returns 
   */
  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
  }

  /**
   * 
   * @param state 
   * @param parent 
   * @returns 
   */
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
    this.datasource = new UatScriptReportDataSource(this.service);
    this.datasource.loadData(0, 10, this.searchedData);
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

  get f() { return this.uatCommunicationSearchForm.controls; }

  download() {
    this.service
      .downloadUatScriptReport(this.searchedData)
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: "text/csv" });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          alert('Please disable your Pop-up blocker and try again.');
        }
      });
  }

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, this.searchedData);
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  loadPage() {
    this.uatCommunicationSearchForm.reset();
    this.searchedData = {};
    this.loadData({});
  }

  formSubmit() {
    if (this.uatCommunicationSearchForm.valid) {
      let technology = this.uatCommunicationSearchForm.controls['searchTechnology'].value;
      let moduleId = this.uatCommunicationSearchForm.controls['searchModuleId'].value;
      let subModuleId = this.uatCommunicationSearchForm.controls['searchSubModuleId'].value;
      let projectId = this.uatCommunicationSearchForm.controls['searchProject'].value;
      let projectUatId = this.uatCommunicationSearchForm.controls['searchUatProjectId'].value;
      let projectUatScriptId = this.uatCommunicationSearchForm.controls['searchUatProjectScriptId'].value;
      //let uploadedByUserId = this.uatCommunicationSearchForm.controls['searchModuleId'].value;
      this.searchedData = {
        "technology": technology == null ? '' : technology,
        "moduleId": moduleId == null ? '' : moduleId,
        "subModuleId": subModuleId == null ? '' : subModuleId,
        "projectId": projectId == null ? '' : projectId,
        "projectUatId": projectUatId == null ? '' : projectUatId,
        "projectUatScriptId": projectUatScriptId == null ? '' : projectUatScriptId,
      };
      //console.log(JSON.stringify(this.searchedData));
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  format(str: String) {
    // var strArray = 
    // strArray = strArray.filter((item) => {
    //   return item !== '';
    // });
    //console.log(strArray.join(",").split(",,").join(","));
    return str.replaceAll(',', '<br/>');
  }

}