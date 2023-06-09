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
import { LoggedInUser } from '../../model/LoggedInUser';
import { range } from 'rxjs';
import { Holiday } from '../../model/Holiday';
declare var $: any;
import * as shajs from 'sha.js';
import { WeeklyIndividualTimeSheet } from './modal/WeeklyIndividualTimeSheet';

@Component({
  selector: 'app-manage-timesheet',
  templateUrl: './manage-timesheet.component.html',
  styleUrls: ['./manage-timesheet.component.scss']
})
export class ManageTimeSheetComponent implements OnInit {
  moduleName: string = "MANAGE TIMESHEET";
  angForm: FormGroup;
  modalRef: BsModalRef;
  hasAgentPermission: boolean;
  userList: any[];
  projectList: any[];
  projectListTmp: Project[];
  projectTypes: any[] = [];
  isHourly: boolean = false;
  alphaNumericRegex = /^[0-9]+$/i;

  private loggedInUser: LoggedInUser;

  private today: moment.Moment;
  private lastDate: moment.Moment;
  private dateRangesGroup: any = {};
  private dateRangesList: any = [];
  private holidays: Holiday[];

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
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();

    this.today = moment().tz(this.loggedInUser.timeZone);
    this.lastDate = moment().tz(this.loggedInUser.timeZone).subtract(6, 'months');

    moment.updateLocale("en", {
      week: {
        // Set the First day of week to Sunday
        dow: 6,
        // Set the First week of year to
        // contain 3rd January
        //doy: 3,
      },
    });

    this.miscService.holidayCalenderHolidaysService(this.loggedInUser.locationId, this.today.format('YYYY'))
      .subscribe((result: Holiday[]) => {
        this.holidays = result;
        for (let k = 0; k < this.holidays.length; k++) {
          this.holidays[k].date = moment(this.holidays[k].date, 'YYYY-MM-DD');
        }

        var range = [];
        var key = null;
        var keyUserHash = null;
        var userId = this.loggedInUser.userId;
        for (let k = 0; k < 12; k++) {
          range = (this.getWeekRange(k == 0 ? k : -k));
          key = this.prepareDateRangesKey(range);
          keyUserHash = userId + " - " + key;
          this.dateRangesGroup[key] = {
            "id": keyUserHash,
            "submited": true,
            "actionTaken": true,
            "approved": false,
            "comment": "NA",
            "inlineMessage": "Submission Pending",
            "offHours": 0.0,
            "dateWiseWorkingHours": [],
          };
          for (let i = 0; i < range.length; i++) {
            this.dateRangesGroup[key].dateWiseWorkingHours.push({
              "date": range[i],
              "formatedDate": range[i].format('DD/MM/YYYY'),
              "day": range[i].day(),
              "month": range[i].month(),
              "year": range[i].year(),
              "week": range[i].week(),
              "isHoliday": this.checkHoliday(range[i]),
              "weekDay": range[i].format('ddd'),
            });
          }
          //console.log(this.getWeekRange(k == 0 ? k : -k));
        }

        // for (const key in this.dateRangesGroup) {
        //   this.dateRangesList.push({
        //     "key": key,
        //     "status": false,
        //     "submitted": false,
        //   })

        // }

        //console.log(this.holidays);
      });

    //var currentWeek = this.getWeekRange(0);
    //var lastWeek = this.getWeekRange(-1);
    //var weekBeforeLast = this.getWeekRange(-2);    
    //var currentWeek2 = this.getWeekRange2(0);

    console.log(this.dateRangesGroup);

    //this.miscService.holidayCalendersService()


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

  }

  viewEdit(weeklyData: Object) {

    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: weeklyData
    };
    this.modalRef = this.modalService.show(WeeklyIndividualTimeSheet,
      Object.assign({}, config, { initialState })
    );

  }

  checkHoliday(day: moment.Moment) {
    var d = moment(day.format('MM/DD/YYYY'));
    var h = null;
    for (let k = 0; k < this.holidays.length; k++) {
      h = moment(moment(this.holidays[k].date).format('MM/DD/YYYY'));
      if (d.isSame(h))
        return true
    }
    return false;
  }

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

  @ViewChild("projectType") projectType;
  maxAllocationDecision(projectType) {
    if (typeof projectType !== 'undefined') {
      this.projectList = [];
      let tmpProj;
      this.angForm.get('maxAllocation').setValue(null);
      this.angForm.get('projects').setValue(null);
      //console.log(this.projectListTmp);
      if (projectType.id == 'HOURLY') {
        this.isHourly = true;
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectType == "HOURLY") {
            tmpProj = this.projectListTmp[k];
            tmpProj.label = tmpProj.name + "[" + tmpProj.code + "]";
            this.projectList.push(tmpProj);
          } else { continue; }
        }
      } else if (projectType.id == 'FULL_BID') {
        this.isHourly = false;
        for (let k = 0; k < this.projectListTmp.length; k++) {
          if (this.projectListTmp[k].projectType == "FULL_BID") {
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

  currentTimeSheet() {
    console.log("currentTimeSheet");
  }

  oldTimeSheets() {
    console.log("oldTimeSheets");
  }

}