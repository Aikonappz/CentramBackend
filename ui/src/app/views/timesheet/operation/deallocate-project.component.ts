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
import { ProjectDeallocateDTO } from '../../../model/ProjectDeallocateDTO';
import { ProjectAllocationDetailDataSource } from '../../../service/datasource/ProjectAllocationDetailDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import { ProjectBillingType } from '../../../model/enumerator/ProjectBillingType';
declare var $: any;

@Component({
  selector: 'app-deallocate-project',
  templateUrl: './deallocate-project.component.html',
  styleUrls: ['./deallocate-project.component.scss']
})
export class DeallocateProjectComponent implements OnInit {
  moduleName: string = "DEALLOCATE PROJECT";
  angForm: FormGroup;
  searchAngForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  userList: any[];
  projectList: any[];
  projectListTmp: Project[];
  projectBillingTypes: any[] = [];
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
      users: new FormControl(null, [
        Validators.required,
      ]),
      projectId: new FormControl(null, [
        Validators.required,
      ]),
    }, {

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
    this.datasource.loadData(0, 10, { "deallocated": 1 });
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
        tap(() => this.loadData({ "deallocated": 1 }))
      )
      .subscribe();
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  get f() { return this.angForm.controls; }
  get sf() { return this.searchAngForm.controls; }

  searchFormSubmit() {
    if (this.searchAngForm.valid) {
      this.loadData({
        "deallocated": 1,
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
      let projectDeallocateDTO: ProjectDeallocateDTO = new ProjectDeallocateDTO();
      projectDeallocateDTO.projectId = this.angForm.controls['projectId'].value;
      projectDeallocateDTO.userIds = this.angForm.controls['users'].value;
      //console.log(projectAllocationDetailList);
      this.callDeallocateProjects(projectDeallocateDTO);
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this.router.navigate(['/asset/ordered']);
  }

  callDeallocateProjects(projectDeallocateDTO: ProjectDeallocateDTO) {
    this.miscService
      .deallocateProjects(projectDeallocateDTO)
      .subscribe((data: any) => {
        const config: ModalOptions = {
          backdrop: 'static',
          keyboard: false,
          animated: true,
          ignoreBackdropClick: true,
          class: 'modal-bg',
        };
        const initialState = {
          msg: "Project deallocated successfully.",
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
      this.angForm.get('projectId').setValue(null);
      //console.log(this.projectListTmp);
      if (projectBillingType.id == 'HOURLY') {
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectBillingType == "HOURLY") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      } else if (projectBillingType.id == 'FULL_BID') {
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectBillingType == "FULL_BID") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      }
    } else {

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

  @ViewChild("projectId") projectId;
  getAllocatedUser(project) {
    if (typeof project !== 'undefined') {
      //console.log(project.id);
      let users = [];
      this.miscService
        .allocatedUsersService({ projectId: project.id })
        .subscribe((data: any) => {
          if (data.content != null && data.content.length > 0) {
            for (let k = 0; k < data.content.length; k++) {
              //console.log(data.content[k].id);
              users.push(data.content[k].id);
            }            
            //console.log(users);
          }
          this.angForm.get('users').setValue(users.map(Number));
        });
    } else {

    }
  }

}