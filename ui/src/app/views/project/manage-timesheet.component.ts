import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder, FormArray } from '@angular/forms';
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
import { LoggedInUser } from '../../model/LoggedInUser';
import { range } from 'rxjs';
import { Holiday } from '../../model/Holiday';

import * as shajs from 'sha.js';
import { WeeklyIndividualTimeSheet } from './modal/WeeklyIndividualTimeSheet';
import { TimeSheet } from '../../model/TimeSheet';
import { TimeSheetDateValidation } from '../../validator/TimeSheetDateValidation';
import { ManageTimeSheetInputVO } from '../../model/ManageTimeSheetInputVO';

declare var $: any;

@Component({
  selector: 'app-manage-timesheet',
  templateUrl: './manage-timesheet.component.html',
  styleUrls: ['./manage-timesheet.component.scss']
})
export class ManageTimeSheetComponent implements OnInit {
  moduleName: string = "MANAGE TIMESHEET";
  angForm: FormGroup;
  timeSheetForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  alphaNumericRegex = /^[0-9]+$/i;
  private loggedInUser: LoggedInUser;
  private timeSheet: TimeSheet;
  private timeSheets: TimeSheet[] = [];
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
      singleDate: new FormControl(null, []),
      rangeDate: new FormControl(null, []),
    });
    this.timeEntrySingleRow = [{ project: null, task: null, locationType: 'OFF', otherTask: null, billingType: null, comment: null, entries: [], }];
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
              billingType: timeEntry.billingType,
              comment: timeEntry.comment,
              entries: this.timeSheetFormBuilder.array(
                timeEntry.entries.map(entry => this.timeSheetFormBuilder.group({
                  date: entry.date,
                  time: null,
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

  get timeEntries() {
    return (<FormArray>this.timeSheetForm.get('timeEntries')).controls;
  }

  getEntries(index) {
    return (<FormArray>(<FormArray>this.timeSheetForm.get('timeEntries')).controls[index].get('entries')).controls;
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
      billingType: null,
      otherTask: null,
      comment: null,
      entries: this.timeSheetFormBuilder.array(
        this.singleRow[0].entries.map(entry => this.timeSheetFormBuilder.group({
          date: entry.date,
          time: null,
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
    this.timeSheets = [];
    let formValues = this.timeSheetForm.value.timeEntries;
    let entries;
    let entryHasError = true;
    for (let i = 0; i < formValues.length; i++) {
      this.timeSheet = new TimeSheet();
      if (this.isEmpty(formValues[i].project)) {
        this.errorText = "Please select project!";
        this.showTimeEntryError(i);
        return;
      } else if (this.isEmpty(formValues[i].task)) {
        this.errorText = "Please select task!";
        this.showTimeEntryError(i);
        return;
      } else if (this.isEmpty(formValues[i].billingType)) {
        this.errorText = "Please select billing type!";
        this.showTimeEntryError(i);
        return;
      } else if (formValues[i].task == "OTHER" && this.isEmpty(formValues[i].otherTask)) {
        this.errorText = "Please enter other task details!";
        this.showTimeEntryError(i);
        return;
      }
      entries = formValues[i].entries;
      for (let e = 0; e < entries.length; e++) {
        entries[e].time = !this.isEmpty(entries[e].time) && entries[e].time.includes("__:__") ? null : entries[e].time;
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
      this.timeSheet = new TimeSheet();
      this.timeSheet.startDate = AppUtility.prepareDateToString(moment(formValues.startDate, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      this.timeSheet.endDate = AppUtility.prepareDateToString(moment(formValues.endDate, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
      this.timeSheet.approved = false;
      this.timeSheet.approver = null;
      this.timeSheet.approverComment = null;
      this.timeSheet.approverTookAction = false;
      this.timeSheet.billingType = formValues[i].billingType;
      this.timeSheet.task = formValues[i].task;
      this.timeSheet.project = project as Project;
      this.timeSheet.referenceId = null;
      this.timeSheet.timeSheetEntries = formValues[i].entries;
      this.timeSheets.push(this.timeSheet);
      this.hideTimeEntryError(i);
    }
    console.log(JSON.stringify(this.timeSheets));
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

  goBack() {
    this.router.navigate(['/project/manage-timesheet']);
  }

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
                }
                myDateRangeTarget.val(dateRange.start + options.rangeSeparator + dateRange.end);
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
            if (myDateRangeTarget.is("input")) {
              myDateRangeTarget.datepicker(options);
            }
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
    } else {
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
          },
        });
      });
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