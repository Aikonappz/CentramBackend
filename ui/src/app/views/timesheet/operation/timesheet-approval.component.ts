import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder, FormArray } from '@angular/forms';
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
import { LoggedInUser } from '../../../model/LoggedInUser';
import { range } from 'rxjs';
import { Holiday } from '../../../model/Holiday';

import * as shajs from 'sha.js';
import { WeeklyIndividualTimeSheet } from '../modal/WeeklyIndividualTimeSheet';
import { TimeSheet, TimeSheetEntry } from '../../../model/TimeSheet';
import { TimeSheetDateValidation } from '../../../validator/TimeSheetDateValidation';
import { ManageTimeSheetInputVO } from '../../../model/ManageTimeSheetInputVO';
import { User } from '../../../model/User';

declare var $: any;

@Component({
  selector: 'app-timesheet-approval',
  templateUrl: './timesheet-approval.component.html',
  styleUrls: ['./timesheet-approval.component.scss']
})
export class TimeSheetApprovalComponent implements OnInit {
  moduleName: string = "TIMESHEET APPROVAL";
  angForm: FormGroup;
  modalRef: BsModalRef;
  private loggedInUser: LoggedInUser;
  manageTimeSheetInputVO: ManageTimeSheetInputVO;
  tasks: string[];
  billingTypes: string[];
  errorText: string;
  newEntity: boolean = true;
  entityId: number;
  timeSheet: TimeSheet;
  actions: any[] = [];
  approverUpdates: FormArray;

  constructor(
    private fb: FormBuilder,
    private timeSheetFormBuilder: FormBuilder,
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
    this.clientStorageService.remove("startDate");
    this.clientStorageService.remove("endDate");
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.actions.push({ id: 1, label: "Approve" });
    this.actions.push({ id: 0, label: "Reject" });
    this.angForm = this.fb.group({
      //approverUpdates: this.fb.array([]),
    });
    this.miscService.getUserProjects()
      .subscribe((result: ManageTimeSheetInputVO) => {
        this.manageTimeSheetInputVO = result;
        //this.projects = this.manageTimeSheetInputVO.projects;
        this.billingTypes = this.manageTimeSheetInputVO.billingTypes;
        this.tasks = this.manageTimeSheetInputVO.tasks;
        //this.locationTypes = this.manageTimeSheetInputVO.locations;
      });
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

  goBack() { this._location.back(); }

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
      //this.type = this.route.snapshot.paramMap.get('licenceType');
      if (!this.route.snapshot.paramMap.has('id')) {
        this.goBack();
      } else {
        this.entityId = Number(this.route.snapshot.paramMap.get('id'));
        this.callTimeSheetService(this.entityId);
      }
    });
  }

  callTimeSheetService(id: number) {
    this.miscService
      .timeSheetService(id)
      .subscribe((data: TimeSheet) => {
        this.timeSheet = data;
        this.timeSheet.timeSheetEntries = this.timeSheet.allTimeSheetEntries;
        // this.procureForm();
        this.angForm = this.fb.group({
          approverUpdates: this.fb.array(
            this.timeSheet.timeSheetEntries.map(timeSheetEntry => this.timeSheetFormBuilder.group({
              approverComment: null,
              action: null,
              entryId: timeSheetEntry.id
            }))
          )
        });
      });
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get f() { return this.angForm.controls; }

  isEmpty(val) {
    return val == null || val == "";
  }

  showTimeEntryError(id) {
    $(function () {
      $('#error-row-' + id).removeClass("d-none");
    });
  }

  hideTimeEntryError(id) {
    $(function () {
      $('#error-row-' + id).addClass("d-none");
    });
  }

  validationRequired(id) {
    for (let k = 0; k < this.timeSheet.timeSheetEntries.length; k++) {
      if (this.timeSheet.timeSheetEntries[k].id == id) {
        return this.timeSheet.timeSheetEntries[k].approverCanTakeAction;
      }
    }
    return true;
  }

  formSubmit() {
    if (this.angForm.valid) {
      let formValues = this.angForm.value.approverUpdates;
      this.timeSheet.timeSheetEntries = this.timeSheet.allTimeSheetEntries;
      this.timeSheet.user = { id: this.timeSheet.user.id, version: this.timeSheet.user.version } as User;
      console.log(formValues);
      for (let i = 0; i < formValues.length; i++) {
        console.log(formValues[i].entryId);
        if (!this.validationRequired(formValues[i].entryId)) {
          continue;
        }
        //console.log(formValues[i].action);
        if (this.isEmpty(formValues[i].approverComment)) {
          this.errorText = "Please add comment!";
          this.showTimeEntryError(i);
          return;
        } else if (formValues[i].action == null) {
          this.errorText = "Please select action!";
          this.showTimeEntryError(i);
          return;
        }
        this.hideTimeEntryError(i);
      }
      for (let k = 0; k < this.timeSheet.timeSheetEntries.length; k++) {
        for (let g = 0; g < formValues.length; g++) {
          if (this.timeSheet.timeSheetEntries[k].id == formValues[g].entryId && this.timeSheet.timeSheetEntries[k].approverCanTakeAction) {
            this.timeSheet.timeSheetEntries[k].approverComment = formValues[g].approverComment;
            if (formValues[g].action == 0) {
              this.timeSheet.timeSheetEntries[k].approved = false;
              this.timeSheet.timeSheetEntries[k].rejected = true;
            } else if (formValues[g].action == 1) {
              this.timeSheet.timeSheetEntries[k].approved = true;
              this.timeSheet.timeSheetEntries[k].rejected = false;
            } else {
              this.timeSheet.timeSheetEntries[k].approved = false;
              this.timeSheet.timeSheetEntries[k].rejected = false;
            }
          }
          for (let key = 0; key < this.billingTypes.length; key++) {
            if (this.billingTypes[key] == this.timeSheet.timeSheetEntries[k].billingType)
              this.timeSheet.timeSheetEntries[k].billingType = key;
          }
          for (let key = 0; key < this.tasks.length; key++) {
            if (this.tasks[key] == this.timeSheet.timeSheetEntries[k].task)
              this.timeSheet.timeSheetEntries[k].task = key;
          }
          this.timeSheet.timeSheetEntries[k].location = 1;
          this.timeSheet.timeSheetEntries[k].project = { id: this.timeSheet.timeSheetEntries[k].project.id, version: this.timeSheet.timeSheetEntries[k].project.version } as Project;
          this.timeSheet.timeSheetEntries[k].approver = { id: this.timeSheet.timeSheetEntries[k].approver.id, version: this.timeSheet.timeSheetEntries[k].approver.version } as User;
        }
      }
      //console.log(this.timeSheet);
      this.timeSheet.allTimeSheetEntries = [];
      this.callSubmitTimesheet(this.timeSheet);
    } else {
      console.log("Invalid Form!");
    }
  }

  callSubmitTimesheet(timeSheet: TimeSheet) {
    this.miscService
      .saveTimesheet(timeSheet)
      .subscribe((data: any) => {
        this.router.navigate(['/timesheet/operation/timesheet-approval-list']);

        // const config: ModalOptions = {
        //   backdrop: 'static',
        //   keyboard: false,
        //   animated: true,
        //   ignoreBackdropClick: true,
        //   class: 'modal-bg',
        // };
        // const initialState = {
        //   msg: "TimeSheet saved successfully.",
        //   url: null,
        // };
        // this.modalRef = this.modalService.show(CommonAlert, Object.assign({}, config, { initialState }));
        // this.angForm.reset();

      });
  }

}