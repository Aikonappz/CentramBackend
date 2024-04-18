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
  selector: 'app-edit-timesheet',
  templateUrl: './edit-timesheet.component.html',
  styleUrls: ['./edit-timesheet.component.scss']
})
export class EditTimeSheetComponent implements OnInit {
  moduleName: string = "TIMESHEET SUBMIT";
  angForm: FormGroup;
  timeSheetForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  alphaNumericRegex = /^[0-9]+$/i;
  private loggedInUser: LoggedInUser;
  private timeSheet: TimeSheet;
  private proxyTimeSheet: TimeSheet;
  dailySelected: boolean = true;
  manageTimeSheetInputVO: ManageTimeSheetInputVO;
  singleRow: any;
  projects: Project[];
  tasks: string[];
  locationTypes: string[];
  billingTypes: string[];
  dateSelected: boolean = false;
  // TODO : have to be configurable
  maxOldDays: number = 60;
  maxDateSubmission: number = 7;
  public mask = {
    guide: true,
    showMask: true,
    mask: [/\d/, /\d/, ':', /\d/, /\d/],
  };
  errorText: string;
  entityId: number;
  holidays: Holiday[];

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
    this.timeSheet = new TimeSheet();
    moment.updateLocale("en", {
      week: {
        // Set the First day of week to Sunday
        dow: 1,
        // Set the First week of year to
        // contain 3rd January
        //doy: 3,
      },
    });
    this.miscService.getUserProjects()
      .subscribe((result: ManageTimeSheetInputVO) => {
        this.manageTimeSheetInputVO = result;
        //this.projects = this.manageTimeSheetInputVO.projects;
        this.billingTypes = this.manageTimeSheetInputVO.billingTypes;
        this.tasks = this.manageTimeSheetInputVO.tasks;
        //this.locationTypes = this.manageTimeSheetInputVO.locations;
      });
    this.miscService
      .holidayCalendarHolidaysService(this.loggedInUser.accountId, this.loggedInUser.locationId, moment().format("YYYY"))
      .subscribe((data: any) => {
        this.holidays = data;
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

  callTimeSheetService(id: number) {
    this.miscService
      .timeSheetService(id)
      .subscribe((data: TimeSheet) => {
        this.timeSheet = data;
        this.proxyTimeSheet = data;
        this.timeSheet.timeSheetEntries = this.timeSheet.allTimeSheetEntries;
        for (let i = 0; i < this.timeSheet.timeSheetEntries.length; i++) {
          this.timeSheet.timeSheetEntries[i].proxyEntries = [];
          for (var key in this.timeSheet.timeSheetEntries[i].timeEntries) {
            this.timeSheet.timeSheetEntries[i].proxyEntries.push({ date: key, time: this.timeSheet.timeSheetEntries[i].timeEntries[key] });
          }
        }
        //console.log(this.timeSheet);
        this.angForm = this.fb.group({
          approverUpdates: this.fb.array(
            this.timeSheet.timeSheetEntries.map(timeSheetEntry => this.fb.group({
              userComment: null,
              action: null,
              entryId: timeSheetEntry.id,
              entries: this.fb.array(
                timeSheetEntry.proxyEntries.map(entry => this.fb.group({
                  isHoliday: this.isHoliday(entry.date),
                  date: entry.date,
                  time: this.isHoliday(entry.date) ? "08:00" : entry.time != null ? moment(entry.time, 'HH:mm:ss').format('HH:mm') : null,
                  day: moment(entry.date, 'YYYY-MM-DD').format('ddd'),
                }))
              ),
            })
            )
          )
        });
      });
  }

  isHoliday(currentDate: any) {
    //console.log(currentDate);
    currentDate = moment(currentDate, AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT);
    let date;
    for (let k = 0; k < this.holidays.length; k++) {
      date = moment(this.holidays[k].date, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
      if (currentDate.isSame(date)) return true;

    }
    return false;
  }

  getEntries(index) {
    return (<FormArray>(<FormArray>this.angForm.get('approverUpdates')).controls[index].get('entries')).controls;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      //console.log(this.route.snapshot.paramMap.get('referer'));
      if (!this.route.snapshot.paramMap.has('id')) {
      } else {
        this.angForm = this.fb.group({
        });
        this.entityId = Number(this.route.snapshot.paramMap.get('id'));
        this.callTimeSheetService(this.entityId);
      }
    });
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log("SUBMITTED", JSON.stringify(this.timeSheet));
      let formValues = this.angForm.value.approverUpdates;
      let entries;
      let entryHasError = true;
      let timeSheetEntry: TimeSheetEntry;
      let timeEntries = [];
      let timeEntry = new Object();

      

      //console.log(formValues);
      for (let g = 0; g < this.timeSheet.timeSheetEntries.length; g++) {
        for (let i = 0; i < formValues.length; i++) {
          if (formValues[i].entryId == this.timeSheet.timeSheetEntries[g].id) {
            entryHasError = true;
            entries = formValues[i].entries;
            timeEntries = [];
            timeEntry = new Object();
            for (let e = 0; e < entries.length; e++) {
              entries[e].time = !this.isEmpty(entries[e].time) && entries[e].time.includes("__:__") ? null : entries[e].time;
              //console.log(entries[e].date);
              //timeEntry[AppUtility.prepareDateToDateString(moment(entries[e].date, AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT).toDate())] = entries[e].time;
              timeEntry[entries[e].date] = entries[e].time;
              //timeEntries.push(timeEntry);
              if (!this.isEmpty(entries[e].time)) {
                entryHasError = false;
              }
            }
            if (entryHasError) {
              this.errorText = "Please correct entered time!";
              this.showTimeEntryError(i);
              return;
            }
            if (this.timeSheet.timeSheetEntries[g].userCanEdit) {
              this.proxyTimeSheet.timeSheetEntries[g].approved = false;
              this.proxyTimeSheet.timeSheetEntries[g].rejected = false;
              this.proxyTimeSheet.timeSheetEntries[g].location = 1;
              this.proxyTimeSheet.timeSheetEntries[g].userComment = formValues[i].userComment;
              //this.proxyTimeSheet.timeSheetEntries[g].approverComment = null;
            }
            for (let key = 0; key < this.billingTypes.length; key++) {
              if (this.billingTypes[key] == this.timeSheet.timeSheetEntries[g].billingType)
                this.proxyTimeSheet.timeSheetEntries[g].billingType = key;
            }
            for (let key = 0; key < this.tasks.length; key++) {
              if (this.tasks[key] == this.timeSheet.timeSheetEntries[g].task)
                this.proxyTimeSheet.timeSheetEntries[g].task = key;
            }
            this.proxyTimeSheet.timeSheetEntries[g].timeEntries = timeEntry;
            this.proxyTimeSheet.timeSheetEntries[g].approver = { id: this.timeSheet.timeSheetEntries[g].approver.id, version: this.timeSheet.timeSheetEntries[g].approver.version } as User;
            this.proxyTimeSheet.timeSheetEntries[g].project = { id: this.timeSheet.timeSheetEntries[g].project.id, version: this.timeSheet.timeSheetEntries[g].project.version } as Project;
            this.hideTimeEntryError(i);
          }
        }
      }
      this.proxyTimeSheet.user = { id: this.timeSheet.user.id, version: this.timeSheet.user.version } as User;
      console.log((this.proxyTimeSheet));
      this.callSubmitTimesheet(this.proxyTimeSheet);


      // for (let i = 0; i < formValues.length; i++) {
      //   entries = formValues[i].entries;
      //   timeEntries = [];
      //   timeEntry = new Object();
      //   //console.log(JSON.stringify(entries));
      //   for (let e = 0; e < entries.length; e++) {
      //     entries[e].time = !this.isEmpty(entries[e].time) && entries[e].time.includes("__:__") ? null : entries[e].time;
      //     timeEntry[AppUtility.prepareDateToDateString(moment(entries[e].date, AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT).toDate())] = entries[e].time;
      //     //timeEntries.push(timeEntry);
      //     if (!this.isEmpty(entries[e].time)) {
      //       entryHasError = false;
      //     }
      //   }
      // if (entryHasError) {
      //   this.errorText = "Please correct entered time!";
      //   this.showTimeEntryError(i);
      //   return;
      // }
      //formValues[i].entries = entries;
      //let project = this.getProject(formValues[i].project);



      // this.timeSheet. = 

      // timeSheetEntry = new TimeSheetEntry();
      // timeSheetEntry.approved = false;
      // timeSheetEntry.rejected = false;
      // timeSheetEntry.rejected = false;
      // timeSheetEntry.approver = null;
      // timeSheetEntry.location = 1;
      // timeSheetEntry.approverComment = null;
      // timeSheetEntry.userComment = formValues[i].comment;
      // timeSheetEntry.project = project as Project;

      // //timeSheetEntry.location = formValues[i].billingType;      
      // timeSheetEntry.timeEntries = timeEntry;
      //console.log((timeSheetEntry));
      //this.timeSheet.timeSheetEntries.push(timeSheetEntry);
      //this.hideTimeEntryError(i);
      // }

      //this.callSubmitTimesheet(this.timeSheet);

    } else {
      console.log("Invalid Form!");
    }
  }

  isEmpty(val) {
    return val == null || val == "";
  }

  callSubmitTimesheet(timeSheet: TimeSheet) {
    this.miscService
      .saveTimesheet(timeSheet)
      .subscribe((data: any) => {
        //console.log(data);
        //this.angForm.reset();
        this.goBack();
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
        // this.timeSheetForm.reset();
        // this.dateSelected = false;

      });
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

  hideAllTimeEntryError() {
    $(function () {
      $('.entry-error').addClass("d-none");
    });
  }

  getProject(id) {
    for (let k = 0; k < this.manageTimeSheetInputVO.projects.length; k++) {
      if (this.manageTimeSheetInputVO.projects[k].id == id) {
        return { id: this.manageTimeSheetInputVO.projects[k].id, version: this.manageTimeSheetInputVO.projects[k].version };
      }
    }
    return null;
  }

  goBack() { this._location.back(); }

  @ViewChild("task") task;
  otherTaskAction(task: string, index: number) {
    if (task == "OTHER") {
      $(function () {
        $("#otherTask_" + index).removeClass("d-none");
      });
    } else {
      $(function () {
        $("#otherTask_" + index).addClass("d-none");
      });
    }
  }

  viewEdit(weeklyData: Object) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl modal-extra-large',
    };
    const initialState = {
      params: weeklyData
    };
    this.modalRef = this.modalService.show(WeeklyIndividualTimeSheet,
      Object.assign({}, config, { initialState })
    );

  }

  // checkHoliday(day: moment.Moment) {
  //   var d = moment(day.format('MM/DD/YYYY'));
  //   var h = null;
  //   for (let k = 0; k < this.holidays.length; k++) {
  //     h = moment(moment(this.holidays[k].date).format('MM/DD/YYYY'));
  //     if (d.isSame(h))
  //       return true
  //   }
  //   return false;
  // }

  getWeekRange(week = 0): moment.Moment[] {
    var weekStart = moment().tz(this.loggedInUser.timeZone).add(week, 'weeks').startOf('week');
    return [...Array(7)].map((_, i) =>
      weekStart.clone().add(i, 'day')
    );
  }

  getWeekRange2(week = 0): moment.Moment[] {
    var weekStart = moment().tz(this.loggedInUser.timeZone).add(week, 'weeks').startOf('week');
    var days = [];
    for (var i = 0; i < 7; i++) {
      days.push(weekStart.clone().add(i, 'day').format('DD/MM/YYYY'));
    }
    return days;
  }

  prepareDateRangesKey(range: moment.Moment[]): string {
    var key = null;
    key = range[0].format('DD/MM/YYYY') + " - " + range[range.length - 1].format('DD/MM/YYYY');
    return key;
  }

  getAllDates(startDate, endDate) {
    var dates = [];
    var currDate = moment(startDate).startOf('day');
    var lastDate = moment(endDate).endOf('day');

    while (currDate.add(1, 'days').diff(lastDate) < 0) {
      console.log(currDate.toDate());
      dates.push(currDate.clone().toDate());
    }

    return dates;
  };

  getToday(): string {
    return new Date().toISOString().split('T')[0]
  }

}