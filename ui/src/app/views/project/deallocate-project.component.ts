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
import { ProjectDeallocateDTO } from '../../model/ProjectDeallocateDTO';
declare var $: any;

@Component({
  selector: 'app-deallocate-project',
  templateUrl: './deallocate-project.component.html',
  styleUrls: ['./deallocate-project.component.scss']
})
export class DeallocateProjectComponent implements OnInit {
  moduleName: string = "DEALLOCATE PROJECT";
  angForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  userList: any[];
  projectList: any[];
  projectListTmp: Project[];
  projectTypes: any[] = [];
  alphaNumericRegex = /^[0-9]+$/i;

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
    this.projectTypes.push({ id: "HOURLY", label: "Hourly" });
    this.projectTypes.push({ id: "FULL_BID", label: "Full Bid" });
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
      projectType: new FormControl(null, [
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

  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  get f() { return this.angForm.controls; }

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

  @ViewChild("projectType") projectType;
  maxAllocationDecision(projectType) {
    if (typeof projectType !== 'undefined') {
      this.projectList = [];
      let tmpProj;
      this.angForm.get('projectId').setValue(null);
      //console.log(this.projectListTmp);
      if (projectType.id == 'HOURLY') {
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectType == "HOURLY") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      } else if (projectType.id == 'FULL_BID') {
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectType == "FULL_BID") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      }
    } else {

    }
  }

}