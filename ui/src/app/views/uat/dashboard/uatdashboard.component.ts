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
declare var $: any;

@Component({
  templateUrl: 'uatdashboard.component.html',
  styleUrls: ['uatdashboard.component.scss']
})
export class UATDashboardComponent implements OnInit {
  roles: string[];
  loggedInUser: LoggedInUser;
  uatDashboardVO: UATDashboardVO = new UATDashboardVO();

  public chartType: ChartType = 'horizontalBar';
  public chartOptions: any;
  public lables: Label[] = ["Total", "Not Started", "In Progress", "Complete"];
  public dataSets: MultiDataSet = [];
  public colors: any[] = [{ backgroundColor: ["#437ff7a8", "#08d620a9", "#f63c6e83", "#8939f283"] }];


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
      .uatDashboard()
      .subscribe((data: UATDashboardVO) => {
        this.uatDashboardVO = data;
        let dataPoints = [];
        let total = this.uatDashboardVO.total;
        dataPoints.push(total);
        dataPoints.push(this.uatDashboardVO.notStarted);
        dataPoints.push(this.uatDashboardVO.inProgress);
        dataPoints.push(this.uatDashboardVO.completed);
        this.dataSets = [dataPoints];
        this.chartOptions = {
          responsive: true, iboxWidth: 1, legend: { display: false }, scales: {
            xAxes: [{
              type: "linear",
              beginAtZero: true,
              ticks: {
                max: Math.ceil(total * 1.05),
                precision: 0
              }
            }]
          }
        }
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

}