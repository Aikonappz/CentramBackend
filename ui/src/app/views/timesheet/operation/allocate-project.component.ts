import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../../service/MiscService';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { MediaService } from '../../../service/MediaService';
import * as moment from 'moment';
import { AppUtility } from '../../../config/AppUtility';
import { ClientStorageService } from '../../../service/ClientStorageService';
import { AssetOrder } from '../../../model/AssetOrder';
import { AssetOrderService } from '../../../service/AssetOrderService';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { CommonAlert } from '../../../containers/default-layout/modal/CommonAlert';
import { UserVO, UserVOListResponse } from '../../../model/UserVO';
import { Project, ProjectList } from '../../../model/Project';
import { ProjectAllocationDetail } from '../../../model/ProjectAllocationDetail';
import { StartEndDateValidation } from '../../../validator/StartEndDateValidation';
import { UatScriptReportDataSource } from '../../../service/datasource/UatScriptReportDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { ProjectAllocationDetailDataSource } from '../../../service/datasource/ProjectAllocationDetailDataSource';
import { tap } from 'rxjs/operators';
import { ProjectBillingType } from '../../../model/enumerator/ProjectBillingType';
declare var $: any;

@Component({
  selector: 'app-allocate-project',
  templateUrl: './allocate-project.component.html',
  styleUrls: ['./allocate-project.component.scss']
})
export class AllocateProjectComponent implements OnInit {
  moduleName: string = "ALLOCATE PROJECT";
  angForm: FormGroup;
  searchAngForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  userList: any[];
  projectList: any[];
  projectList_s: any[];
  projectListTmp: Project[];
  projectBillingTypes: any[] = [];
  isHourly: boolean = false;
  alphaNumericRegex = /^[0-9]+$/i;

  displayedColumns = ['project', 'user', 'duration', 'alocationDate'];
  datasource: ProjectAllocationDetailDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;

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
    this.projectBillingTypes.push({ id: "HOURLY", label: "Hourly" });
    this.projectBillingTypes.push({ id: "FULL_BID", label: "Full Bid" });
    this.userService.getUsersService()
      .subscribe((result: UserVOListResponse) => {
        this.userList = result.content;
        for (let k = 0; k < this.userList.length; k++) {
          this.userList[k].label = this.userList[k].fullName + "[" + this.userList[k].employeeId + "]";
        }
        //console.log(this.userList);
      });
    this.miscService.projectsService()
      .subscribe((result: ProjectList) => {
        this.projectListTmp = result.content;
      });
    this.angForm = this.fb.group({
      projectBillingType: new FormControl(null, [
        Validators.required,
      ]),
      maxAllocation: new FormControl(null, [
        Validators.required,
        Validators.maxLength(10),
        Validators.pattern(this.alphaNumericRegex),
      ]),
      users: new FormControl(null, [
        Validators.required,
      ]),
      projects: new FormControl(null, [
        Validators.required,
      ]),
      start: new FormControl(moment().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
        Validators.required,
      ]),
      end: new FormControl(moment().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
        Validators.required,
      ]),
    }, {
      validators: StartEndDateValidation('start', 'end')
    });

    this.searchAngForm = this.fb.group({
      projectBillingType_s: new FormControl(null, [
        Validators.required,
      ]),
      projects_s: new FormControl(null, [
        Validators.required,
      ]),
      start_s: new FormControl(moment().subtract(90, 'd').format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
        Validators.required,
      ]),
      end_s: new FormControl(moment().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT), [
        Validators.required,
      ]),
    }, {
      validators: StartEndDateValidation('start_s', 'end_s')
    });

  }

  getToday(): string {
    return new Date().toISOString().split('T')[0]
  }

  hasPermission(actions: string): boolean {
    let modules = this.moduleName.split(",");
    let actionList = actions.split(",");
    for (let i in modules) {
      if (this.loggedInUserService.hasPermissionByName(modules[i], actionList[i])) {
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
    this.datasource = new ProjectAllocationDetailDataSource(this.miscService);
    this.datasource.loadData(0, 10, {});
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

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  get f() { return this.angForm.controls; }
  get sf() { return this.searchAngForm.controls; }

  getProject(id: number) {
    for (let o = 0; o < this.projectList.length; o++) {
      if (this.projectList[o].id == id) {
        return { id: this.projectList[o].id, code: this.projectList[o].code, version: this.projectList[o].version };
      }
      continue;
    }
    return null;
  }

  getUser(id: number) {
    for (let o = 0; o < this.userList.length; o++) {
      if (this.userList[o].id == id) {
        return { id: this.userList[o].id, version: this.userList[o].version };
      }
      continue;
    }
    return null;
  }

  searchFormSubmit() {
    if (this.searchAngForm.valid) {
      this.loadData({
        "billingType": ProjectBillingType[this.searchAngForm.controls['projectBillingType_s'].value],
        "projects": this.searchAngForm.controls['projects_s'].value,
        "start": AppUtility.prepareDateToString(moment(this.searchAngForm.controls['start_s'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate()),
        "end": AppUtility.prepareDateToString(moment(this.searchAngForm.controls['end_s'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate()),
      });
    } else {
      console.log("Invalid Form!");
    }
  }

  formSubmit() {
    if (this.angForm.valid) {
      let projectAllocationDetailList: ProjectAllocationDetail[] = [];
      let projectAllocationDetail: ProjectAllocationDetail = null;
      let selectedProject: any[] = this.angForm.controls['projects'].value;
      let selectedUser: any[] = this.angForm.controls['users'].value;
      let counter = 0;
      let start = null;
      let end = null;
      let mnt = null;
      start = AppUtility.prepareDateToString(moment(this.angForm.controls['start'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      end = AppUtility.prepareDateToString(moment(this.angForm.controls['end'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      for (let i = 0; i < selectedProject.length; i++) {
        for (let k = 0; k < selectedUser.length; k++) {
          projectAllocationDetail = new ProjectAllocationDetail();
          projectAllocationDetail.maxAllocation = this.angForm.controls['maxAllocation'].value;
          projectAllocationDetail.project = this.getProject(selectedProject[i]);
          projectAllocationDetail.user = this.getUser(selectedUser[k]);
          projectAllocationDetail.startDate = start;
          projectAllocationDetail.endDate = end;
          //console.log(projectAllocationDetail.user);
          projectAllocationDetailList[counter++] = projectAllocationDetail;
        }
      }
      //console.log(projectAllocationDetailList);
      this.callAllocateProjects(projectAllocationDetailList);
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this.router.navigate(['/asset/ordered']);
  }

  callAllocateProjects(projectAllocationDetailList: ProjectAllocationDetail[]) {
    this.miscService
      .allocateProjects(projectAllocationDetailList)
      .subscribe((data: any) => {
        const config: ModalOptions = {
          backdrop: 'static',
          keyboard: false,
          animated: true,
          ignoreBackdropClick: true,
          class: 'modal-bg',
        };
        const initialState = {
          msg: "Project allocated successfully.",
          url: null,
        };
        this.modalRef = this.modalService.show(CommonAlert, Object.assign({}, config, { initialState }));
        this.angForm.reset();
      });
  }

  @ViewChild("projectBillingType") projectBillingType;
  maxAllocationDecision(projectBillingType) {
    if (typeof projectBillingType !== 'undefined') {
      this.projectList = [];
      let tmpProj;
      this.angForm.get('maxAllocation').setValue(null);
      this.angForm.get('projects').setValue(null);
      console.log(this.projectListTmp);
      if (projectBillingType.id == 'HOURLY') {
        this.isHourly = true;
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectBillingType == "HOURLY") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      } else if (projectBillingType.id == 'FULL_BID') {
        this.isHourly = false;
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectBillingType == "FULL_BID") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      }
    } else {
      this.isHourly = false;
    }
  }

  @ViewChild("projectBillingType_s") projectBillingType_s;
  deriveProjects(projectBillingType_s) {
    if (typeof projectBillingType_s !== 'undefined') {
      this.projectList_s = [];
      let tmpProj;
      this.searchAngForm.get('projects_s').setValue(null);
      console.log(this.projectListTmp);
      if (projectBillingType_s.id == 'HOURLY') {
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectBillingType == "HOURLY") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList_s.push(tmpProj);
          } else { continue; }
        }
      } else if (projectBillingType_s.id == 'FULL_BID') {
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectBillingType == "FULL_BID") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList_s.push(tmpProj);
          } else { continue; }
        }
      }
    } else {
      this.searchAngForm.get('projects_s').setValue(null);
    }
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }

}