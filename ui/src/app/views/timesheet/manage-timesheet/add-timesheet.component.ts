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

declare var $: any;

@Component({
  selector: 'app-add-timesheet',
  templateUrl: './add-timesheet.component.html',
  styleUrls: ['./add-timesheet.component.scss']
})
export class AddTimeSheetComponent implements OnInit {
  moduleName: string = "TIMESHEET SUBMIT";
  angForm: FormGroup;
  timeSheetForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  alphaNumericRegex = /^[0-9]+$/i;
  private loggedInUser: LoggedInUser;
  private timeSheet: TimeSheet;
  dailySelected: boolean = true;
  manageTimeSheetInputVO: ManageTimeSheetInputVO;
  timeEntrySingleRow: any;
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










    // this.today = moment().tz(this.loggedInUser.timeZone);
    // this.lastDate = moment().tz(this.loggedInUser.timeZone).subtract(6, 'months');



    // this.miscService.holidayCalendarHolidaysService(this.loggedInUser.accountId, this.loggedInUser.locationId, this.today.format('YYYY'))
    //   .subscribe((result: Holiday[]) => {
    //     this.holidays = result;
    //     for (let k = 0; k < this.holidays.length; k++) {
    //       this.holidays[k].date = moment(this.holidays[k].date, 'YYYY-MM-DD');
    //     }

    //     var range = [];
    //     var key = null;
    //     var keyUserHash = null;
    //     var userId = this.loggedInUser.userId;
    //     for (let k = 0; k < 12; k++) {
    //       range = (this.getWeekRange(k == 0 ? k : -k));
    //       key = this.prepareDateRangesKey(range);
    //       keyUserHash = userId + " - " + key;
    //       this.dateRangesGroup[key] = {
    //         "id": keyUserHash,
    //         "submited": true,
    //         "actionTaken": true,
    //         "approved": false,
    //         "comment": "NA",
    //         "inlineMessage": "Submission Pending",
    //         "offHours": 0.0,
    //         "dateWiseWorkingHours": [],
    //       };
    //       for (let i = 0; i < range.length; i++) {
    //         this.dateRangesGroup[key].dateWiseWorkingHours.push({
    //           "date": range[i],
    //           "formatedDate": range[i].format('DD/MM/YYYY'),
    //           "day": range[i].day(),
    //           "month": range[i].month(),
    //           "year": range[i].year(),
    //           "week": range[i].week(),
    //           "isHoliday": this.checkHoliday(range[i]),
    //           "weekDay": range[i].format('ddd'),
    //         });
    //       }
    //       //console.log(this.getWeekRange(k == 0 ? k : -k));
    //     }

    //     // for (const key in this.dateRangesGroup) {
    //     //   this.dateRangesList.push({
    //     //     "key": key,
    //     //     "status": false,
    //     //     "submitted": false,
    //     //   })

    //     // }

    //     //console.log(this.holidays);
    //   });

    //var currentWeek = this.getWeekRange(0);
    //var lastWeek = this.getWeekRange(-1);
    //var weekBeforeLast = this.getWeekRange(-2);    
    //var currentWeek2 = this.getWeekRange2(0);

    // console.log(this.dateRangesGroup);



    // this.projectTypes.push({ id: "HOURLY", label: "Hourly" });
    // this.projectTypes.push({ id: "FULL_BID", label: "Full Bid" });
    // this.userService.getUsersService()
    //   .subscribe((result: UserVOListResponse) => {
    //     this.userList = result.content;
    //     for (let k = 0; k < this.userList.length; k++) {
    //       this.userList[k].label = this.userList[k].fullName + "[" + this.userList[k].employeeId + "]";
    //     }
    //     //console.log(this.userList);
    //   });
    // this.miscService.projectsService()
    //   .subscribe((result: ProjectList) => {
    //     this.projectListTmp = result.content;
    //   });

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

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      //console.log(this.route.snapshot.paramMap.get('referer'));
    });
    this.angForm = this.fb.group({
      submissionSelection: new FormControl('DAILY', []),
      //singleDate: new FormControl(null, []),
      //rangeDate: new FormControl(null, []),
      //weeklyDate: new FormControl(null, []),
    });
    this.timeEntrySingleRow = [{ project: null, task: null, locationType: 'OFF', otherTask: null, billable: 0, comment: null, entries: [], }];
    this.timeSheetForm = this.timeSheetFormBuilder.group({
      timeEntries: this.timeSheetFormBuilder.array([]),
    });
    this.manageTimeSheetInputVO = new ManageTimeSheetInputVO();
    this.miscService.getUserProjects()
      .subscribe((result: ManageTimeSheetInputVO) => {
        this.manageTimeSheetInputVO = result;
        this.projects = this.manageTimeSheetInputVO.projects;
        this.billingTypes = this.manageTimeSheetInputVO.billingTypes;
        this.tasks = this.manageTimeSheetInputVO.tasks;
        //this.locationTypes = this.manageTimeSheetInputVO.locations;
      });
    $(function () {
      $("#singleDate").datepicker({
        dateFormat: 'dd/mm/yy',
        orientation: "top auto",
        firstDay: 1,
        minDate: '-1M',
        maxDate: 0,
        onSelect: function (dateText, inst) {
          AppUtility.storage.setItem(btoa("startDate"), btoa(dateText));
          AppUtility.storage.setItem(btoa("endDate"), btoa(dateText));
        },
      });
    });

  }



  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      if (this.clientStorageService.get("startDate") == "" && this.clientStorageService.get("endDate") == "" || this.clientStorageService.get("startDate") == null && this.clientStorageService.get("endDate") == null) {
        alert("Please select date to submit time sheet!");
        return;
      }
      this.timeSheet.startDate = this.clientStorageService.get("startDate");
      this.timeSheet.endDate = this.clientStorageService.get("endDate");
      // moment.updateLocale('en', {
      //   weekdaysShort:
      //     ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"]
      // });
      let mnt = moment(this.timeSheet.startDate, AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT);
      //this.timeSheet.startDate = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
      this.timeSheet.startDate = mnt.format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
      mnt = moment(this.timeSheet.endDate, AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT);
      //this.timeSheet.endDate = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
      this.timeSheet.endDate = mnt.format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
      this.populateInputVO(this.timeSheet.startDate, this.timeSheet.endDate).then(res => {
        //console.log(this.timeEntrySingleRow);
        this.timeSheetForm = this.timeSheetFormBuilder.group({
          timeEntries: this.timeSheetFormBuilder.array(
            this.timeEntrySingleRow.map(timeEntry => this.timeSheetFormBuilder.group({
              project: timeEntry.project,
              task: timeEntry.task,
              locationType: 'OFF',
              otherTask: null,
              billable: 0,
              comment: timeEntry.comment,
              entries: this.timeSheetFormBuilder.array(
                timeEntry.entries.map(entry => this.timeSheetFormBuilder.group({
                  isHoliday: this.isHoliday(entry.date),
                  date: entry.date,
                  time: this.isHoliday(entry.date) ? "08:00" : null,
                  day: moment(entry.date, 'DD/MM/YYYY').format('ddd'),
                }))
              ),
            }))
          )
        });
        this.dateSelected = true;
        //this.timeSheetForm.get("timeEntries");
        //console.log(this.timeSheetForm.get("timeEntries"));
      });
    } else {
      console.log("Invalid Form!");
    }
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

  // checkHoliday(entry) {
  //   console.log(entry);
  //   return false;
  // }

  get timeEntries() {
    return (<FormArray>this.timeSheetForm.get('timeEntries')).controls;
  }

  getEntries(index) {
    return (<FormArray>(<FormArray>this.timeSheetForm.get('timeEntries')).controls[index].get('entries')).controls;
  }

  checkIsHoliday(index, childIndex) {
    //console.log((<FormArray>(<FormArray>this.timeSheetForm.get('timeEntries')).controls[index].get('entries')).controls[childIndex].get("isHoliday").value);
    return (<FormArray>(<FormArray>this.timeSheetForm.get('timeEntries')).controls[index].get('entries')).controls[childIndex].get("isHoliday").value;
  }

  private populateInputVO(startDate, endDate) {
    this.manageTimeSheetInputVO.timeEntries = [];
    while (moment(startDate) <= moment(endDate)) {
      this.manageTimeSheetInputVO.timeEntries.push({
        "time": '00.00', "date": moment(startDate).format(AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT),
      });
      startDate = moment(startDate).add(1, 'days').format("YYYY-MM-DD");
    }
    this.timeEntrySingleRow[0].entries = [];
    for (let i = 0; i < this.manageTimeSheetInputVO.timeEntries.length; i++) {
      this.timeEntrySingleRow[0].entries[i] = this.manageTimeSheetInputVO.timeEntries[i];
    }
    this.singleRow = this.timeEntrySingleRow;
    //console.log(JSON.stringify(this.singleRow));
    return new Promise<void>((resolve, reject) => { console.log('populateInputVO'); resolve(); });
  }

  addTimeEntry() {
    //console.log(JSON.stringify(this.singleRow[0].entries));
    (<FormArray>this.timeSheetForm.get('timeEntries')).push(this.timeSheetFormBuilder.group({
      project: null,
      task: null,
      locationType: 'OFF',
      billable: 0,
      otherTask: null,
      comment: null,
      entries: this.timeSheetFormBuilder.array(
        this.singleRow[0].entries.map(entry => this.timeSheetFormBuilder.group({
          isHoliday: this.isHoliday(entry.date),
          date: entry.date,
          time: this.isHoliday(entry.date) ? "08:00" : null,
          day: moment(entry.date, 'DD/MM/YYYY').format('ddd'),
        }))
      ),
    }));
  }

  removeTimeEntry(i: number) {
    (<FormArray>this.timeSheetForm.get('timeEntries')).removeAt(i);
  }

  isEmpty(val) {
    return val == null || val == "";
  }

  onSubmit() {
    let formValues = this.timeSheetForm.value.timeEntries;
    let entries;
    let entryHasError = true;
    this.timeSheet = new TimeSheet();
    this.timeSheet.startDate = AppUtility.prepareDateToDateString(moment(this.clientStorageService.get("startDate"), AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT).toDate());
    this.timeSheet.endDate = AppUtility.prepareDateToDateString(moment(this.clientStorageService.get("endDate"), AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT).toDate());
    this.timeSheet.timeSheetEntries = [];
    let timeSheetEntry: TimeSheetEntry;
    let timeEntries = [];
    let timeEntry = new Object();
    //console.log(formValues);
    for (let i = 0; i < formValues.length; i++) {
      if (this.isEmpty(formValues[i].project)) {
        this.errorText = "Please select project!";
        this.showTimeEntryError(i);
        return;
      } else if (this.isEmpty(formValues[i].task)) {
        this.errorText = "Please select task!";
        this.showTimeEntryError(i);
        return;
      }
      // else if (this.isEmpty(formValues[i].billingType)) {
      //   this.errorText = "Please select billing type!";
      //   this.showTimeEntryError(i);
      //   return;
      // } 
      else if (formValues[i].task == "OTHER" && this.isEmpty(formValues[i].otherTask)) {
        this.errorText = "Please enter other task details!";
        this.showTimeEntryError(i);
        return;
      }
      entries = formValues[i].entries;
      timeEntries = [];
      timeEntry = new Object();
      //console.log(JSON.stringify(entries));
      for (let e = 0; e < entries.length; e++) {
        entries[e].time = !this.isEmpty(entries[e].time) && entries[e].time.includes("__:__") ? null : entries[e].time;
        timeEntry[AppUtility.prepareDateToDateString(moment(entries[e].date, AppUtility.APP_VIEW_DATEPICKER_INP_DATE_FORMAT).toDate())] = entries[e].time;
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
      formValues[i].entries = entries;
      let project = this.getProject(formValues[i].project);
      if (project == null) {
        this.errorText = "Invalid project selected!";
        this.showTimeEntryError(i);
        return;
      }
      timeSheetEntry = new TimeSheetEntry();
      timeSheetEntry.approved = false;
      timeSheetEntry.rejected = false;
      timeSheetEntry.rejected = false;
      timeSheetEntry.approver = null;
      timeSheetEntry.location = 1;
      timeSheetEntry.approverComment = null;
      timeSheetEntry.userComment = formValues[i].comment;
      timeSheetEntry.project = project as Project;
      for (let key = 0; key < this.tasks.length; key++) {
        if (this.tasks[key] == formValues[i].task)
          timeSheetEntry.task = key;
      }
      // for (let key = 0; key < this.billingTypes.length; key++) {
      //   if (this.billingTypes[key] == formValues[i].billingType)
      //     timeSheetEntry.billingType = key;
      // }
      //timeSheetEntry.location = formValues[i].billingType;      
      timeSheetEntry.timeEntries = timeEntry;
      //console.log((timeSheetEntry));
      this.timeSheet.timeSheetEntries.push(timeSheetEntry);
      timeSheetEntry.billingType = formValues[i].billable ? 0 : 1;
      this.hideTimeEntryError(i);
    }
    //console.log(JSON.stringify(this.timeSheet));
    this.callSubmitTimesheet(this.timeSheet);
  }

  callSubmitTimesheet(timeSheet: TimeSheet) {
    this.miscService
      .saveTimesheet(timeSheet)
      .subscribe((data: any) => {
        //console.log(data);
        //this.angForm.reset();

        const config: ModalOptions = {
          backdrop: 'static',
          keyboard: false,
          animated: true,
          ignoreBackdropClick: true,
          class: 'modal-bg',
        };
        const initialState = {
          msg: "TimeSheet saved successfully.",
          url: null,
        };
        this.modalRef = this.modalService.show(CommonAlert, Object.assign({}, config, { initialState }));
        this.angForm.reset();
        this.timeSheetForm.reset();
        this.dateSelected = false;
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

  Cancel() {
    //this.angForm.reset();
    window.location.reload();
  }

  @ViewChild("submissionSelection") submissionSelection;
  onChange(submissionSelection: string) {
    //console.log(submissionSelection);
    this.timeSheet.submissionSelection = submissionSelection;
    if (submissionSelection == "RANGE") {
      (function ($) {
        function compareDates(startDate, endDate, format) {
          var temp, dateStart, dateEnd;
          try {
            dateStart = $.datepicker.parseDate(format, startDate);
            dateEnd = $.datepicker.parseDate(format, endDate);
            if (dateEnd < dateStart) {
              temp = startDate;
              startDate = endDate;
              endDate = temp;
            }
          } catch (ex) { }
          return { start: startDate, end: endDate };
        }
        $.fn.dateRangePicker = function (options) {
          options = $.extend({
            "changeMonth": false,
            "changeYear": false,
            "numberOfMonths": 2,
            "rangeSeparator": " - ",
            "useHiddenAltFields": false,
          }, options || {});

          var myDateRangeTarget = $(this);
          var onSelect = options.onSelect || $.noop;
          var onClose = options.onClose || $.noop;
          var beforeShow = options.beforeShow || $.noop;
          var beforeShowDay = options.beforeShowDay;
          var lastDateRange;

          function storePreviousDateRange(dateText, dateFormat) {
            var start, end;
            dateText = dateText.split(options.rangeSeparator);
            if (dateText.length > 0) {
              start = $.datepicker.parseDate(dateFormat, dateText[0]);
              if (dateText.length > 1) {
                end = $.datepicker.parseDate(dateFormat, dateText[1]);
              }
              lastDateRange = { start: start, end: end };
            } else {
              lastDateRange = null;
            }
          }

          options.beforeShow = function (input, inst) {
            var dateFormat = myDateRangeTarget.datepicker("option", "dateFormat");
            console.log(AppUtility.storage.get(atob("startDate")), AppUtility.storage.get(atob("endDate")));
            storePreviousDateRange($(input).val(), dateFormat);
            beforeShow.apply(myDateRangeTarget, arguments);
          };

          options.beforeShowDay = function (date) {
            var out = [true, ""], extraOut;
            if (lastDateRange && lastDateRange.start <= date) {
              if (lastDateRange.end && date <= lastDateRange.end) {
                out[1] = "ui-datepicker-range";
              }
            }

            if (beforeShowDay) {
              extraOut = beforeShowDay.apply(myDateRangeTarget, arguments);
              out[0] = out[0] && extraOut[0];
              out[1] = out[1] + " " + extraOut[1];
              out[2] = extraOut[2];
            }
            return out;
          };

          function loopCallender() {
            $("#rangeDate .ui-datepicker-calendar tbody tr").each(function () {
              if ($(this).has("td")) {
                $(this).children("td").each(function () {
                  let d, m: string;
                  let mn: number;
                  let date;
                  let from = moment(atob(localStorage.getItem(btoa("startDate"))), 'DD/MM/YYYY');
                  let to = moment(atob(localStorage.getItem(btoa("endDate"))), 'DD/MM/YYYY');
                  if ($(this).has("a")) {
                    if ($(this).find('a').hasClass("ui-state-default") && typeof $(this).find('a').data("date") !== 'undefined' && $(this).find('a').data("date") > 0) {
                      let aHref = $(this).children('a')[0];
                      //console.log($(aHref).data("date"));
                      d = $(aHref).data("date").toString();
                      mn = $(this).data("month") + 1;
                      m = mn.toString();
                      date = moment(d.padStart(2, "0") + "/" + m.padStart(2, "0") + "/" + $(this).data("year"), 'DD/MM/YYYY');
                      //console.log(from, to);
                      if (date.isBetween(from, to)) {
                        $(aHref).addClass('ui-state-active');
                        //console.log(date);
                      } else if (date.isSame(from)) {
                        $(aHref).addClass('ui-state-active');
                        //console.log(date);
                      } else if (date.isSame(to)) {
                        $(aHref).addClass('ui-state-active');
                        //console.log(date);
                      } else {
                        $(aHref).removeClass('ui-state-active');
                      }
                    }
                  }
                });
              }
            });
          }

          options.onSelect = function (dateText, inst) {
            var textStart;
            if (!inst.rangeStart) {
              inst.inline = true;
              inst.rangeStart = dateText;
            } else {
              inst.inline = false;
              textStart = inst.rangeStart;
              if (textStart !== dateText) {
                var dateFormat = myDateRangeTarget.datepicker("option", "dateFormat");
                var dateRange = compareDates(textStart, dateText, dateFormat);
                if (options.useHiddenAltFields) {
                  // var myToField = myDateRangeTarget.attr("data-to-field");
                  // var myFromField = myDateRangeTarget.attr("data-from-field");
                  // $("#" + myFromField).val(dateRange.start);
                  // $("#" + myToField).val(dateRange.end);
                  if (moment(dateRange.end, 'DD/MM/YYYY').diff(moment(dateRange.start, 'DD/MM/YYYY'), 'days') > 6) {
                    alert("Maximum 1 week timesheet submission allowed!")
                    return;
                  }
                  AppUtility.storage.setItem(btoa("startDate"), btoa(dateRange.start));
                  AppUtility.storage.setItem(btoa("endDate"), btoa(dateRange.end));
                  //alert("asasa");
                  //$(this).datepicker("refresh");
                  loopCallender();
                }
                //myDateRangeTarget.val(dateRange.start + options.rangeSeparator + dateRange.end);
                inst.rangeStart = null;
              }
            }
            onSelect.apply(myDateRangeTarget, arguments);
          };

          options.onClose = function (dateText, inst) {
            inst.rangeStart = null;
            inst.inline = false;
            onClose.apply(myDateRangeTarget, arguments);
          };

          return this.each(function () {
            //if (myDateRangeTarget.is("input")) {
            myDateRangeTarget.datepicker(options);
            //}
            myDateRangeTarget.wrap("<div class=\"dateRangeWrapper\"></div>");
          });
        };
      }($));
      $(function () {
        $("#rangeDate").dateRangePicker({
          showOn: "focus",
          rangeSeparator: " to ",
          dateFormat: "dd/mm/yy",
          useHiddenAltFields: true,
          constrainInput: true,
          orientation: "top auto",
          firstDay: 1,
          minDate: '-30D',
          maxDate: 7,
        });
      });
      this.dailySelected = false;
    } else if (submissionSelection == "DAILY") {
      this.dailySelected = true;
      $(function () {
        $("#singleDate").datepicker({
          dateFormat: 'dd/mm/yy',
          orientation: "top auto",
          firstDay: 1,
          minDate: '-30D',
          maxDate: 7,
          onSelect: function (dateText, inst) {
            AppUtility.storage.setItem(btoa("startDate"), btoa(dateText));
            AppUtility.storage.setItem(btoa("endDate"), btoa(dateText));
            //console.log(dateText);
          },
        });
      });
    } else {
      $(function () {
        var startDate;
        var endDate;
        var selectCurrentWeek = function () {
          window.setTimeout(function () {
            $('#weeklyDate').find('.ui-datepicker-current-day a').addClass('ui-state-active')
          }, 1);
        }
        $('#weeklyDate').datepicker({
          showOtherMonths: true,
          selectOtherMonths: true,
          onSelect: function (dateText, inst) {
            var date = $(this).datepicker('getDate');
            startDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay());
            endDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 6);
            var dateFormat = inst.settings.dateFormat || $.datepicker._defaults.dateFormat;
            //console.log(moment(startDate).format('DD/MM/YYYY'), moment(endDate).format('DD/MM/YYYY'));
            AppUtility.storage.setItem(btoa("startDate"), btoa(moment(startDate).format('DD/MM/YYYY')));
            AppUtility.storage.setItem(btoa("endDate"), btoa(moment(endDate).format('DD/MM/YYYY')));
            selectCurrentWeek();
          },
          beforeShowDay: function (date) {
            var cssClass = '';
            if (date >= startDate && date <= endDate)
              cssClass = 'ui-datepicker-current-day';
            return [true, cssClass];
          },
          onChangeMonthYear: function (year, month, inst) {
            selectCurrentWeek();
          }
        });
      });
      $('#weeklyDate .ui-datepicker-calendar').on('mousemove', 'tr', function () { $(this).find('td a').addClass('ui-state-hover'); });
      $('#weeklyDate .ui-datepicker-calendar').on('mouseleave', 'tr', function () { $(this).find('td a').removeClass('ui-state-hover'); });
    }
  }

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