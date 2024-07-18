import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';


import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { DashboardService } from '../../../service/DashboardService';
import { AdminDashboardVO } from '../../../model/AdminDashboardVO';
import { UATDashboardVO } from '../../../model/UATDashboardVO';
import { Label, MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';
import { ViewTimesheetDashboardDetails } from './modal/ViewTimesheetDashboardDetails';
import { TimesheetDashboardVO } from '../../../model/TimesheetDashboardVO';
declare var $: any;

@Component({
  templateUrl: 'dashboard.component.html',
  styleUrls: ['dashboard.component.scss']
})
export class TimeSheetDashboardComponent implements OnInit {
  roles: string[];
  loggedInUser: LoggedInUser;
  timesheetDashboardVO: TimesheetDashboardVO = new TimesheetDashboardVO();
  public uatChartType = 'horizontalBar';
  public uatChartOptions: any = {
    responsive: true, iboxWidth: 1, legend: { display: false }, scales: {
      xAxes: [{
        type: "linear",
        beginAtZero: true,
        ticks: {
          max: Math.ceil(10 * 1.05),
          precision: 0
        }
      }]
    }
  };
  public lables: Label[] = ["Modules in Total", "Modules Not Started", "Modules In-Progress", "Modules Completed"];
  public dataSets: MultiDataSet = [];
  public colors: any[] = [{ backgroundColor: ["#437ff7a8", "#08d620a9", "#f63c6e83", "#8939f283"] }];
  modalRef: BsModalRef;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private service: DashboardService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.roles = this.loggedInUser.roles;
    //console.log(this.loggedInUser.licenseType);
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
    this.service
      .timesheetDashboard()
      .subscribe((data: TimesheetDashboardVO) => {
        this.timesheetDashboardVO = data;
        let dataPoints = [];
        //let total = this.timesheetDashboardVO.total;
        //dataPoints.push(total);
        dataPoints.push(this.timesheetDashboardVO.approvedCount);
        dataPoints.push(this.timesheetDashboardVO.pendingCount);
        this.dataSets = [dataPoints];
        // this.uatChartOptions = {
        //   responsive: true, iboxWidth: 1, legend: { display: false }, scales: {
        //     xAxes: [{
        //       type: "linear",
        //       beginAtZero: true,
        //       ticks: {
        //         max: Math.ceil(total * 1.05),
        //         precision: 0
        //       }
        //     }]
        //   }
        // };
        $(function () {
          $("#dataSets-app-admin").accordion({
            //icons: { "header": "ui-icon-plus", "activeHeader": "ui-icon-minus" },
            heightStyle: "content",
            active: true,
            collapsible: true,
            activate: function (event, ui) {
              var index = $(this).accordion("option", "active");
              //console.log(index);
            }
          });
          $(".accordion-toggle:eq(0)").trigger('click');
        });
      });
  }

  chunk(arr, size) {
    var newArr = [];
    for (var i = 0; i < arr.length; i += size) {
      newArr.push(arr.slice(i, i + size));
    }
    return newArr;
  }

  chartHovered(e: any) {
    //this.getChartSegmentData(e);
  }

  siteAdminChart1(e: any) {
    // if (this.getChartSegmentData(e) === "Total Organizations") {
    //   this.viewSiteAdmin({ "status": "ALL" });
    // } else if (this.getChartSegmentData(e) === "Active Organizations") {
    //   this.viewSiteAdmin({ "status": "ACTIVE" })
    // } else if (this.getChartSegmentData(e) === "Inactive Organizations") {
    //   this.viewSiteAdmin({ "status": "INACTIVE" })
    // } else {
    //   this.viewSiteAdmin({ "status": "ALL" });
    // }
  }



  viewSiteAdmin(element: any) {
    // const config: ModalOptions = {
    //   backdrop: 'static',
    //   keyboard: false,
    //   animated: true,
    //   ignoreBackdropClick: true,
    //   class: 'modal-xl',
    // };
    // const initialState = {
    //   params: element
    // };
    // this.modalRef = this.modalService.show(ViewAppAdminDashboardDetails,
    //   Object.assign({}, config, { initialState })
    // );
  }

  viewTimesheet(element: any) {
    //alert(element);
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: element.status === 'approved' ? { approved: true } : { approved: false },
    };
    this.modalRef = this.modalService.show(ViewTimesheetDashboardDetails,
      Object.assign({}, config, { initialState })
    );
  }

  uatChart(e: any) {
    // if (this.getChartSegmentData(e) == "Modules in Total") {
    //   this.viewUat({ status: "total" });
    // } else if (this.getChartSegmentData(e) == "Modules Not Started") {
    //   this.viewUat({ status: "notStarted" });
    // } else if (this.getChartSegmentData(e) == "Modules In-Progress") {
    //   this.viewUat({ status: "inProgress" });
    // } else if (this.getChartSegmentData(e) == "Modules Completed") {
    //   this.viewUat({ status: "completed" });
    // }
  }

  getChartSegmentData(e: any) {
    if (e.active.length > 0) {
      const chart = e.active[0]._chart;
      const activePoints = chart.getElementAtEvent(e.event);
      if (activePoints.length > 0) {
        // get the internal index of slice in pie chart
        const clickedElementIndex = activePoints[0]._index;
        const label = chart.data.labels[clickedElementIndex];
        // get value by index
        const value = chart.data.datasets[0].data[clickedElementIndex];
        //console.log(chart.data);
        //console.log(clickedElementIndex, label, value);
        return label;
      }
    }
  }

}