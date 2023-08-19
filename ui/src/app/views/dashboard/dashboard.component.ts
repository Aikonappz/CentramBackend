import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { DashboardService } from '../../service/DashboardService';
import { AdminDashboardVO } from '../../model/AdminDashboardVO';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import * as moment from 'moment';
import { Label, MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';
import { OrgAdminDashboardVO } from '../../model/OrgAdminDashboardVO';
import { UserDashboardVO } from '../../model/UserDashboardVO';
import { AgentDashboardVO } from '../../model/AgentDashboardVO';
import { CategoryAdminDashboardVO } from '../../model/CategoryAdminDashboardVO';
import { ViewIncidentDetails } from './modal/ViewIncidentDetails';
import { ViewAppAdminDashboardDetails } from './modal/ViewAppAdminDashboardDetails';
import { ViewOrgAdminDashboardUserDetails } from './modal/ViewOrgAdminDashboardUserDetails';
import { ViewOrgAdminDashboardVendorDetails } from './modal/ViewOrgAdminDashboardVendorDetails';
import { ViewUserDashboardDetails } from './modal/ViewUserDashboardDetails';
declare var $: any;

@Component({
  templateUrl: 'dashboard.component.html',
  styleUrls: ['dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  roles: string[];
  loggedInUser: LoggedInUser;
  adminDashboard: AdminDashboardVO = new AdminDashboardVO();
  orgAdminDashboardVO: OrgAdminDashboardVO = new OrgAdminDashboardVO(null);
  userDashboardVO: UserDashboardVO = new UserDashboardVO(null);
  agentDashboardVO: AgentDashboardVO = new AgentDashboardVO(null);
  categoryAdminDashboardVO: CategoryAdminDashboardVO = new CategoryAdminDashboardVO(null);
  modalRef: BsModalRef;

  firstTabLoaded: boolean = false;

  orgAdminTilesData: any[] = [];
  orgAdminChunkedTilesData: any[] = [];
  orgAdmin1TilesData: any[] = [];
  orgAdmin1ChunkedTilesData: any[] = [];
  orgAdmin2TilesData: any[] = [];
  orgAdmin2ChunkedTilesData: any[] = [];
  orgAdmin3TilesData: any[] = [];
  orgAdmin3ChunkedTilesData: any[] = [];

  userTilesData: any[] = [];
  userChunkedTilesData: any[] = [];
  user1TilesData: any[] = [];
  user1ChunkedTilesData: any[] = [];
  user2TilesData: any[] = [];
  user2ChunkedTilesData: any[] = [];
  user3TilesData: any[] = [];
  user3ChunkedTilesData: any[] = [];

  agentTilesData: any[] = [];
  agentChunkedTilesData: any[] = [];
  agent1TilesData: any[] = [];
  agent1ChunkedTilesData: any[] = [];
  agent2TilesData: any[] = [];
  agent2ChunkedTilesData: any[] = [];
  agent3TilesData: any[] = [];
  agent3ChunkedTilesData: any[] = [];
  agent4TilesData: any[] = [];
  agent4ChunkedTilesData: any[] = [];
  agent5TilesData: any[] = [];
  agent5ChunkedTilesData: any[] = [];

  caTilesData: any[] = [];
  caChunkedTilesData: any[] = [];
  ca1TilesData: any[] = [];
  ca1ChunkedTilesData: any[] = [];
  ca2TilesData: any[] = [];
  ca2ChunkedTilesData: any[] = [];
  ca3TilesData: any[] = [];
  ca3ChunkedTilesData: any[] = [];
  ca4TilesData: any[] = [];
  ca4ChunkedTilesData: any[] = [];
  ca5TilesData: any[] = [];
  ca5ChunkedTilesData: any[] = [];
  ca6TilesData: any[] = [];
  ca6ChunkedTilesData: any[] = [];

  public chartType: ChartType = 'pie';
  public chartOptions: any = { responsive: true, iboxWidth: 1, legend: { position: 'top' } };

  public siteAdminChart1Labels: Label[] = [];
  public siteAdminChart1Data: MultiDataSet = [];
  public siteAdminChart1Colors: any[] = [{ backgroundColor: ["#437ff7a8", "#08d620a9", "#f63c6e83"] }];

  public siteAdminChart2Labels: Label[] = [];
  public siteAdminChart2Data: MultiDataSet = [];
  public siteAdminChart2Colors: any[] = [{ backgroundColor: ["#437ff7a8", "#08d620a9", "#f63c6e83", "#b2b429a4", "#584acf83"] }];

  public orgAdminChart1Labels: Label[] = [];
  public orgAdminChart1Data: MultiDataSet = [];
  public hasOrgAdminChart1Data: boolean = false;
  public orgAdminChart1Colors: any[] = [{ backgroundColor: ["#f46f3e93", "#19e1b69d", "#6e5eeca9", "#18cb0f96", "#b652daa2", "#CF755D", "#D2B6CE", "#D6F094", "#AAEEDE", "#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public orgAdminChart2Labels: Label[] = [];
  public orgAdminChart2Data: MultiDataSet = [];
  public hasOrgAdminChart2Data: boolean = false;
  public orgAdminChart2Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];

  public orgAdminChart3Labels: Label[] = [];
  public orgAdminChart3Data: MultiDataSet = [];
  public hasOrgAdminChart3Data: boolean = false;
  public orgAdminChart3Colors: any[] = [{ backgroundColor: ["#2cb51da6", "#11B7B3", "#e43451a4", "#B4B6FA", "#F5B9CA", "#F890B5", "#D2B6CE", "#D6F094", "#AAEEDE", "#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public orgAdminChart4Labels: Label[] = [];
  public orgAdminChart4Data: MultiDataSet = [];
  public hasOrgAdminChart4Data: boolean = false;
  public orgAdminChart4Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];

  public userChart1Labels: Label[] = [];
  public userChart1Data: MultiDataSet = [];
  public hasUserChart1Data: boolean = false;
  public userChart1Colors: any[] = [{ backgroundColor: ["#f46f3e93", "#19e1b69d", "#6e5eeca9", "#18cb0f96", "#b652daa2", "#CF755D", "#D2B6CE", "#D6F094", "#AAEEDE", "#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public userChart2Labels: Label[] = [];
  public userChart2Data: MultiDataSet = [];
  public hasUserChart2Data: boolean = false;
  public userChart2Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];

  public userChart3Labels: Label[] = [];
  public userChart3Data: MultiDataSet = [];
  public hasUserChart3Data: boolean = false;
  public userChart3Colors: any[] = [{ backgroundColor: ["#2cb51da6", "#11B7B3", "#e43451a4", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public userChart4Labels: Label[] = [];
  public userChart4Data: MultiDataSet = [];
  public hasUserChart4Data: boolean = false;
  public userChart4Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];

  public agentChart1Labels: Label[] = [];
  public agentChart1Data: MultiDataSet = [];
  public hasAgentChart1Data: boolean = false;
  public agentChart1Colors: any[] = [{ backgroundColor: ["#f46f3e93", "#19e1b69d", "#6e5eeca9", "#18cb0f96", "#b652daa2", "#CF755D", "#D2B6CE", "#D6F094", "#AAEEDE", "#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public agentChart2Labels: Label[] = [];
  public agentChart2Data: MultiDataSet = [];
  public hasAgentChart2Data: boolean = false;
  public agentChart2Colors: any[] = [{ backgroundColor: ["#437ff7a8", "#f63c6e83", "#08d620a9", "#27BFCB",] }];

  public agentChart3Labels: Label[] = [];
  public agentChart3Data: MultiDataSet = [];
  public hasAgentChart3Data: boolean = false;
  public agentChart3Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];

  public agentChart4Labels: Label[] = [];
  public agentChart4Data: MultiDataSet = [];
  public hasAgentChart4Data: boolean = false;
  public agentChart4Colors: any[] = [{ backgroundColor: ["#2cb51da6", "#11B7B3", "#e43451a4", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public agentChart5Labels: Label[] = [];
  public agentChart5Data: MultiDataSet = [];
  public hasAgentChart5Data: boolean = false;
  public agentChart5Colors: any[] = [{ backgroundColor: ["#437ff7a8", "#f63c6e83", "#08d620a9", "#27BFCB",] }];

  public agentChart6Labels: Label[] = [];
  public agentChart6Data: MultiDataSet = [];
  public hasAgentChart6Data: boolean = false;
  public agentChart6Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];;

  public categoryAdminChart1Labels: Label[] = [];
  public categoryAdminChart1Data: MultiDataSet = [];
  public hasCategoryAdminChart1Data: boolean = false;
  public categoryAdminChart1Colors: any[] = [{ backgroundColor: ["#f46f3e93", "#19e1b69d", "#6e5eeca9", "#18cb0f96", "#b652daa2", "#CF755D", "#D2B6CE", "#D6F094", "#AAEEDE", "#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"] }];

  public categoryAdminChart2Labels: Label[] = [];
  public categoryAdminChart2Data: MultiDataSet = [];
  public hasCategoryAdminChart2Data: boolean = false;
  public categoryAdminChart2Colors: any[] = [{ backgroundColor: ["#437ff7a8", "#f63c6e83", "#08d620a9", "#27BFCB",] }];

  public categoryAdminChart3Labels: Label[] = [];
  public categoryAdminChart3Data: MultiDataSet = [];
  public hasCategoryAdminChart3Data: boolean = false;
  public categoryAdminChart3Colors: any[] = [{ backgroundColor: ["#f46f3e93", "#19e1b69d", "#6e5eeca9", "#18cb0f96", "#b652daa2", "#ffc107", "#17a2b8", "#20c997", "#ffc107", "#f86c6b", "#6FC8CE",] }];

  public categoryAdminChart4Labels: Label[] = [];
  public categoryAdminChart4Data: MultiDataSet = [];
  public hasCategoryAdminChart4Data: boolean = false;
  public categoryAdminChart4Colors: any[] = [{ backgroundColor: ["#e43451a4", "#2cb51da6",] }];

  public categoryAdminChart5Labels: Label[] = [];
  public categoryAdminChart5Data: MultiDataSet = [];
  public hasCategoryAdminChart5Data: boolean = false;
  public categoryAdminChart5Colors: any[] = [{ backgroundColor: ["#3DA8D8", "#E9518B", "#FAC008", "#7048C1", "#fd72e6", "#ffc107", "#17a2b8", "#20c997", "#ffc107", "#f86c6b", "#6FC8CE",] }];

  public categoryAdminChart6Labels: Label[] = [];
  public categoryAdminChart6Data: MultiDataSet = [];
  public hasCategoryAdminChart6Data: boolean = false;
  public categoryAdminChart6Colors: any[] = [{ backgroundColor: ["#F8CB0F", "#E9528D", "#5FBE76", "#7249C2", "#3EA9D8", "#ffc107", "#17a2b8", "#20c997", "#ffc107", "#f86c6b", "#6FC8CE",] }];

  public categoryAdminChart7Labels: Label[] = [];
  public categoryAdminChart7Data: MultiDataSet = [];
  public hasCategoryAdminChart7Data: boolean = false;
  public categoryAdminChart7Colors: any[] = [{ backgroundColor: ["#42A3B8", "#adff2f", "#7048C1", "#63CA96", "#f86c6b", "#FAC008", "#3B5998", "#E9518B", "#63C2DE", "#ffc107", "#F8CB00",] }];

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
    this.loadData();
  }

  getIncidentStatus(s: string): string {
    let status = "";
    if (s == "Open") {
      status = "OPEN";
    } else if (s == "Assigned") {
      status = "ASSIGNED";
    } else if (s == "Need Clarification") {
      status = "NEED_CLARIFICATION";
    } else if (s == "Work In Progress") {
      status = "WORK_IN_PROGRESS";
    } else if (s == "Closed") {
      status = "CLOSED";
    } else if (s == "Sla About To Breach") {
      status = "SLA_ABOUT_TO_BREACH";
    } else if (s == "Sla Breached") {
      status = "SLA_BREACHED";
    } else if (s == "On Hold") {
      status = "ON_HOLD";
    } else if (s == "Pending from vendor") {
      status = "PENDING_FROM_VENDOR";
    } else if (s == "Clarification provided") {
      status = "CLARIFICATION_PROVIDED";
    }
    return status;
  }

  loadData() {
    if (this.roles.includes('APP_ADMIN')) {
      this.service
        .appAdminDashboard()
        .subscribe((data: AdminDashboardVO) => {
          this.adminDashboard = data;
          let dataPoints = [];
          let total = this.adminDashboard.activeCompanies + this.adminDashboard.inactiveCompanies;
          dataPoints.push(total);
          dataPoints.push(this.adminDashboard.activeCompanies);
          dataPoints.push(this.adminDashboard.inactiveCompanies);
          this.siteAdminChart1Labels = [
            "Total Organizations",
            "Active Organizations",
            "Inactive Organizations",
          ];
          this.siteAdminChart1Data = [dataPoints];

          dataPoints = [];
          //total = this.adminDashboard.allLicenceTypeCompanies + this.adminDashboard.assetLicenceTypeCompanies + this.adminDashboard.incidentLicenceTypeCompanies;
          //dataPoints.push(total);
          dataPoints.push(this.adminDashboard.allLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.assetLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.incidentLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.projectLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.uatLicenceTypeCompanies);
          // this.admin1DoughnutChartLabels = [
          //   "Total",
          //   "All License Type",
          //   "Asset License Type",
          //   "Incident License Type",
          // ];
          this.siteAdminChart2Labels = [
            "License Type - All",
            "License Type - Asset",
            "License Type - Incident",
            "License Type - Project",
            "License Type - UAT",
          ];
          this.siteAdminChart2Data = [dataPoints];
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
      this.firstTabLoaded = true;
    }
    if (this.roles.includes('ORG_ADMIN')) {
      if (this.firstTabLoaded === false) {
        this.firstTabLoaded = true;
        this.service
          .orgAdminDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
          .subscribe((data: OrgAdminDashboardVO) => {
            this.orgAdminDashboardVO = new OrgAdminDashboardVO(data);
            let dataPoints = [];
            dataPoints.push(this.orgAdminDashboardVO.activeEmployees);
            dataPoints.push(this.orgAdminDashboardVO.inHouseVendors);
            dataPoints.push(this.orgAdminDashboardVO.outSourcedVendors);

            dataPoints = [];
            this.orgAdminTilesData = [];
            for (let i in this.orgAdminDashboardVO.moduleWiseIncidents) {
              this.orgAdminTilesData[i] = {
                moduleId: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleId,
                name: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleName,
                value: this.orgAdminDashboardVO.moduleWiseIncidents[i].count || 0,
                backgroundColour: this.orgAdminChart1Colors[0].backgroundColor[i],
                detailDataParams: { moduleId: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleId, incidentType: "INCIDENT" }
              };
              dataPoints.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].count);
              this.orgAdminChart1Labels.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleName);
              if (this.hasOrgAdminChart1Data == false) {
                this.hasOrgAdminChart1Data = this.orgAdminDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.orgAdminChunkedTilesData = this.chunk(this.orgAdminTilesData, 5);
            this.orgAdminChart1Data = [dataPoints];

            dataPoints = [];
            this.orgAdmin1TilesData = [];
            for (let i in this.orgAdminDashboardVO.statusWiseIncidents) {
              this.orgAdmin1TilesData[i] = {
                name: this.orgAdminDashboardVO.statusWiseIncidents[i].status,
                value: this.orgAdminDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.orgAdminChart2Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                  allClosed: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                  incidentType: "INCIDENT"
                  //status: this.getIncidentStatus(this.orgAdminDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.orgAdminDashboardVO.statusWiseIncidents[i].count);
              this.orgAdminChart2Labels.push(this.orgAdminDashboardVO.statusWiseIncidents[i].status);
              if (this.hasOrgAdminChart2Data == false) {
                this.hasOrgAdminChart2Data = this.orgAdminDashboardVO.statusWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.orgAdmin1ChunkedTilesData = this.chunk(this.orgAdmin1TilesData, 5);
            this.orgAdminChart2Data = [dataPoints];

            dataPoints = [];
            this.orgAdmin2TilesData = [];
            for (let i in this.orgAdminDashboardVO.moduleWiseAssetIncidents) {
              this.orgAdmin2TilesData[i] = {
                moduleId: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                name: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleName,
                value: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].count || 0,
                backgroundColour: this.orgAdminChart3Colors[0].backgroundColor[i],
                detailDataParams: { moduleId: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleId, incidentType: "ASSET" }
              };
              dataPoints.push(this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].count);
              this.orgAdminChart3Labels.push(this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleName);
              if (this.hasOrgAdminChart3Data == false) {
                this.hasOrgAdminChart3Data = this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.orgAdmin2ChunkedTilesData = this.chunk(this.orgAdmin2TilesData, 5);
            this.orgAdminChart3Data = [dataPoints];

            dataPoints = [];
            this.orgAdmin3TilesData = [];
            for (let i in this.orgAdminDashboardVO.statusWiseAssetIncidents) {
              this.orgAdmin3TilesData[i] = {
                name: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status,
                value: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].count || 0,
                backgroundColour: this.orgAdminChart4Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status == "Open" ? true : false,
                  allClosed: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status == "Closed" ? true : false,
                  incidentType: "ASSET"
                  //status: this.getIncidentStatus(this.orgAdminDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.orgAdminDashboardVO.statusWiseAssetIncidents[i].count);
              this.orgAdminChart4Labels.push(this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status);
              if (this.hasOrgAdminChart4Data == false) {
                this.hasOrgAdminChart4Data = this.orgAdminDashboardVO.statusWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.orgAdmin3ChunkedTilesData = this.chunk(this.orgAdmin3TilesData, 5);
            this.orgAdminChart4Data = [dataPoints];
            $(function () {
              $("#dataSets-org-admin").accordion({
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
    }
    if (this.hasUserRole()) {
      if (this.firstTabLoaded === false) {
        this.firstTabLoaded = true;
        this.service
          .userDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
          .subscribe((data: UserDashboardVO) => {
            this.userDashboardVO = new UserDashboardVO(data);
            let dataPoints = [];
            this.userTilesData = [];
            for (let i in this.userDashboardVO.moduleWiseIncidents) {
              this.userTilesData[i] = {
                moduleId: this.userDashboardVO.moduleWiseIncidents[i].moduleId,
                name: this.userDashboardVO.moduleWiseIncidents[i].moduleName,
                value: this.userDashboardVO.moduleWiseIncidents[i].count || 0,
                backgroundColour: this.userChart1Colors[0].backgroundColor[i],
                detailDataParams: { moduleId: this.userDashboardVO.moduleWiseIncidents[i].moduleId, raisedUserId: this.loggedInUser.userId, incidentType: "INCIDENT" }
              };
              dataPoints.push(this.userDashboardVO.moduleWiseIncidents[i].count);
              this.userChart1Labels.push(this.userDashboardVO.moduleWiseIncidents[i].moduleName);
              if (this.hasUserChart1Data == false) {
                this.hasUserChart1Data = this.userDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.userChunkedTilesData = this.chunk(this.userTilesData, 5);
            this.userChart1Data = [dataPoints];

            dataPoints = [];
            this.user2TilesData = [];
            for (let i in this.userDashboardVO.moduleWiseAssetIncidents) {
              this.user2TilesData[i] = {
                moduleId: this.userDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                name: this.userDashboardVO.moduleWiseAssetIncidents[i].moduleName,
                value: this.userDashboardVO.moduleWiseAssetIncidents[i].count || 0,
                backgroundColour: this.userChart3Colors[0].backgroundColor[i],
                detailDataParams: { moduleId: this.userDashboardVO.moduleWiseAssetIncidents[i].moduleId, raisedUserId: this.loggedInUser.userId, incidentType: "ASSET" }
              };
              dataPoints.push(this.userDashboardVO.moduleWiseAssetIncidents[i].count);
              this.userChart3Labels.push(this.userDashboardVO.moduleWiseAssetIncidents[i].moduleName);
              if (this.hasUserChart3Data == false) {
                this.hasUserChart3Data = this.userDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.user2ChunkedTilesData = this.chunk(this.user2TilesData, 5);
            this.userChart3Data = [dataPoints];

            dataPoints = [];
            this.user1TilesData = [];
            for (let i in this.userDashboardVO.statusWiseIncidents) {
              this.user1TilesData[i] = {
                name: this.userDashboardVO.statusWiseIncidents[i].status,
                value: this.userDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.userChart2Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.userDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                  allClosed: this.userDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                  incidentType: "INCIDENT",
                  raisedUserId: this.loggedInUser.userId,
                  //status: this.getIncidentStatus(this.userDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.userDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.userDashboardVO.statusWiseIncidents[i].count);
              this.userChart2Labels.push(this.userDashboardVO.statusWiseIncidents[i].status);
              if (this.hasUserChart2Data == false) {
                this.hasUserChart2Data = this.userDashboardVO.statusWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.user1ChunkedTilesData = this.chunk(this.user1TilesData, 5);
            this.userChart2Data = [dataPoints];

            dataPoints = [];
            this.user3TilesData = [];
            for (let i in this.userDashboardVO.statusWiseAssetIncidents) {
              this.user3TilesData[i] = {
                name: this.userDashboardVO.statusWiseAssetIncidents[i].status,
                value: this.userDashboardVO.statusWiseAssetIncidents[i].count || 0,
                backgroundColour: this.userChart4Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.userDashboardVO.statusWiseAssetIncidents[i].status == "Open" ? true : false,
                  allClosed: this.userDashboardVO.statusWiseAssetIncidents[i].status == "Closed" ? true : false,
                  incidentType: "ASSET",
                  raisedUserId: this.loggedInUser.userId,
                  //status: this.getIncidentStatus(this.userDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.userDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.userDashboardVO.statusWiseAssetIncidents[i].count);
              this.userChart4Labels.push(this.userDashboardVO.statusWiseAssetIncidents[i].status);
              if (this.hasUserChart4Data == false) {
                this.hasUserChart4Data = this.userDashboardVO.statusWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.user3ChunkedTilesData = this.chunk(this.user3TilesData, 5);
            this.userChart4Data = [dataPoints];
            $(function () {
              $("#dataSets-org-user").accordion({
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
    }
    if (this.hasAgentRole()) {
      if (this.firstTabLoaded === false) {
        this.firstTabLoaded = true;
        this.service
          .agentDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
          .subscribe((data: AgentDashboardVO) => {
            this.agentDashboardVO = new AgentDashboardVO(data);
            let dataPoints = [];
            this.agentTilesData = [];
            for (let i in this.agentDashboardVO.moduleWiseIncidents) {
              this.agentTilesData[i] = {
                moduleId: this.agentDashboardVO.moduleWiseIncidents[i].moduleId,
                name: this.agentDashboardVO.moduleWiseIncidents[i].moduleName,
                value: this.agentDashboardVO.moduleWiseIncidents[i].count || 0,
                backgroundColour: this.agentChart1Colors[0].backgroundColor[i],
                detailDataParams: {
                  moduleId: this.agentDashboardVO.moduleWiseIncidents[i].moduleId,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                }
              };
              dataPoints.push(this.agentDashboardVO.moduleWiseIncidents[i].count);
              this.agentChart1Labels.push(this.agentDashboardVO.moduleWiseIncidents[i].moduleName);
              if (this.hasAgentChart1Data == false) {
                this.hasAgentChart1Data = this.agentDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.agentChunkedTilesData = this.chunk(this.agentTilesData, 5);
            this.agentChart1Data = [dataPoints];

            dataPoints = [];
            this.agent3TilesData = [];
            for (let i in this.agentDashboardVO.moduleWiseAssetIncidents) {
              this.agent3TilesData[i] = {
                moduleId: this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                name: this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleName,
                value: this.agentDashboardVO.moduleWiseAssetIncidents[i].count || 0,
                backgroundColour: this.agentChart4Colors[0].backgroundColor[i],
                detailDataParams: {
                  moduleId: this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                }
              };
              dataPoints.push(this.agentDashboardVO.moduleWiseAssetIncidents[i].count);
              this.agentChart4Labels.push(this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleName);
              if (this.hasAgentChart4Data == false) {
                this.hasAgentChart4Data = this.agentDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.agent3ChunkedTilesData = this.chunk(this.agent3TilesData, 5);
            this.agentChart4Data = [dataPoints];

            dataPoints = [];
            this.agent1TilesData = [];
            for (let i in this.agentDashboardVO.priorityWiseIncidents) {
              this.agent1TilesData[i] = {
                priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
                name: this.agentDashboardVO.priorityWiseIncidents[i].priority,
                value: this.agentDashboardVO.priorityWiseIncidents[i].count || 0,
                backgroundColour: this.agentChart2Colors[0].backgroundColor[i],
                detailDataParams: {
                  priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                }
              };
              dataPoints.push(this.agentDashboardVO.priorityWiseIncidents[i].count);
              this.agentChart2Labels.push(this.agentDashboardVO.priorityWiseIncidents[i].priority);
              if (this.hasAgentChart2Data == false) {
                this.hasAgentChart2Data = this.agentDashboardVO.priorityWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.agent1ChunkedTilesData = this.chunk(this.agent1TilesData, 5);
            this.agentChart2Data = [dataPoints];

            dataPoints = [];
            this.agent4TilesData = [];
            for (let i in this.agentDashboardVO.priorityWiseAssetIncidents) {
              this.agent4TilesData[i] = {
                priorityId: this.agentDashboardVO.priorityWiseAssetIncidents[i].priorityId,
                name: this.agentDashboardVO.priorityWiseAssetIncidents[i].priority,
                value: this.agentDashboardVO.priorityWiseAssetIncidents[i].count || 0,
                backgroundColour: this.agentChart5Colors[0].backgroundColor[i],
                detailDataParams: {
                  priorityId: this.agentDashboardVO.priorityWiseAssetIncidents[i].priorityId,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                }
              };
              dataPoints.push(this.agentDashboardVO.priorityWiseAssetIncidents[i].count);
              this.agentChart5Labels.push(this.agentDashboardVO.priorityWiseAssetIncidents[i].priority);
              if (this.hasAgentChart5Data == false) {
                this.hasAgentChart5Data = this.agentDashboardVO.priorityWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.agent4ChunkedTilesData = this.chunk(this.agent4TilesData, 5);
            this.agentChart5Data = [dataPoints];


            dataPoints = [];
            this.agent2TilesData = [];
            for (let i in this.agentDashboardVO.statusWiseIncidents) {
              this.agent2TilesData[i] = {
                name: this.agentDashboardVO.statusWiseIncidents[i].status,
                value: this.agentDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.agentChart3Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.agentDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                  allClosed: this.agentDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                  //status: this.getIncidentStatus(this.agentDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.agentDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.agentDashboardVO.statusWiseIncidents[i].count);
              this.agentChart3Labels.push(this.agentDashboardVO.statusWiseIncidents[i].status);
              if (this.hasAgentChart3Data == false) {
                this.hasAgentChart3Data = this.agentDashboardVO.statusWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.agent2ChunkedTilesData = this.chunk(this.agent2TilesData, 5);
            this.agentChart3Data = [dataPoints];

            dataPoints = [];
            this.agent5TilesData = [];
            for (let i in this.agentDashboardVO.statusWiseAssetIncidents) {
              this.agent5TilesData[i] = {
                name: this.agentDashboardVO.statusWiseAssetIncidents[i].status,
                value: this.agentDashboardVO.statusWiseAssetIncidents[i].count || 0,
                backgroundColour: this.agentChart6Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.agentDashboardVO.statusWiseAssetIncidents[i].status == "Open" ? true : false,
                  allClosed: this.agentDashboardVO.statusWiseAssetIncidents[i].status == "Closed" ? true : false,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                  //status: this.getIncidentStatus(this.agentDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.agentDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.agentDashboardVO.statusWiseAssetIncidents[i].count);
              this.agentChart6Labels.push(this.agentDashboardVO.statusWiseAssetIncidents[i].status);
              if (this.hasAgentChart6Data == false) {
                this.hasAgentChart6Data = this.agentDashboardVO.statusWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.agent5ChunkedTilesData = this.chunk(this.agent5TilesData, 5);
            this.agentChart6Data = [dataPoints];

            $(function () {
              $("#dataSets-org-agent").accordion({
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
    }
    if (this.hasCategoryAdminRole()) {
      if (this.firstTabLoaded === false) {
        this.firstTabLoaded = true;
        this.service
          .categoryAdminDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
          .subscribe((data: CategoryAdminDashboardVO) => {
            this.categoryAdminDashboardVO = new CategoryAdminDashboardVO(data);
            let dataPoints = [];
            this.caTilesData = [];
            for (let i in this.categoryAdminDashboardVO.moduleWiseIncidents) {
              this.caTilesData[i] = {
                moduleId: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleId,
                name: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleName,
                value: this.categoryAdminDashboardVO.moduleWiseIncidents[i].count || 0,
                backgroundColour: this.categoryAdminChart1Colors[0].backgroundColor[i],
                detailDataParams: {
                  moduleId: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleId,
                  assignedUserId: "",
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].count);
              this.categoryAdminChart1Labels.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleName);
              if (this.hasCategoryAdminChart1Data == false) {
                this.hasCategoryAdminChart1Data = this.categoryAdminDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
              }
            }
            this.caChunkedTilesData = this.chunk(this.caTilesData, 5);
            this.categoryAdminChart1Data = [dataPoints];

            dataPoints = [];
            this.ca4TilesData = [];
            for (let i in this.categoryAdminDashboardVO.moduleWiseAssetIncidents) {
              this.ca4TilesData[i] = {
                moduleId: this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                name: this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].moduleName,
                value: this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].count || 0,
                backgroundColour: this.categoryAdminChart5Colors[0].backgroundColor[i],
                detailDataParams: {
                  moduleId: this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                  assignedUserId: "",
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].count);
              this.categoryAdminChart5Labels.push(this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].moduleName);
              if (this.hasCategoryAdminChart5Data == false) {
                this.hasCategoryAdminChart5Data = this.categoryAdminDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
              }
            }
            this.ca4ChunkedTilesData = this.chunk(this.ca4TilesData, 3);
            this.categoryAdminChart5Data = [dataPoints];
            dataPoints = [];
            this.ca1TilesData = [];
            for (let i in this.categoryAdminDashboardVO.priorityWiseIncidents) {
              this.ca1TilesData[i] = {
                priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
                name: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority,
                value: this.categoryAdminDashboardVO.priorityWiseIncidents[i].count || 0,
                backgroundColour: this.categoryAdminChart2Colors[0].backgroundColor[i],
                detailDataParams: {
                  priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
                  assignedUserId: "",
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].count);
              this.categoryAdminChart2Labels.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority);
            }
            this.ca1ChunkedTilesData = this.chunk(this.ca1TilesData, 5);
            this.categoryAdminChart2Data = [dataPoints];

            dataPoints = [];
            this.ca3TilesData = [];
            for (let i in this.categoryAdminDashboardVO.statusWiseIncidents) {
              this.ca3TilesData[i] = {
                name: this.categoryAdminDashboardVO.statusWiseIncidents[i].status,
                value: this.categoryAdminDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.categoryAdminChart4Colors[0].backgroundColor[i],
                detailDataParams: {
                  allOpen: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                  allClosed: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                  //status: this.getIncidentStatus(this.categoryAdminDashboardVO.statusWiseIncidents[i].status),
                  //escalated1stLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  //escalated2ndLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  //isReopened: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].count);
              this.categoryAdminChart4Labels.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].status);
            }
            this.ca3ChunkedTilesData = this.chunk(this.ca3TilesData, 5);
            this.categoryAdminChart4Data = [dataPoints];

            dataPoints = [];
            dataPoints.push(this.categoryAdminDashboardVO.aging5);
            dataPoints.push(this.categoryAdminDashboardVO.aging10);
            dataPoints.push(this.categoryAdminDashboardVO.aging20);
            dataPoints.push(this.categoryAdminDashboardVO.aging30);
            dataPoints.push(this.categoryAdminDashboardVO.aging60);
            this.categoryAdminChart3Data = [dataPoints];
            this.categoryAdminChart3Labels = [
              " > 5 Days",
              " > 10 Days",
              " > 20 Days",
              " > 30 Days",
              " > 60 Days",
            ];
            $(function () {
              $("#dataSets-org-category-admin").accordion({
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
    }
  }

  loadAppAdminData() {
    if (this.adminDashboard.activeCompanies > -1) {
      this.service
        .appAdminDashboard()
        .subscribe((data: AdminDashboardVO) => {
          this.adminDashboard = data;
          let dataPoints = [];
          let total = this.adminDashboard.activeCompanies + this.adminDashboard.inactiveCompanies;
          dataPoints.push(total);
          dataPoints.push(this.adminDashboard.activeCompanies);
          dataPoints.push(this.adminDashboard.inactiveCompanies);
          this.siteAdminChart1Labels = [
            "Total Organizations",
            "Active Organizations",
            "Inactive Organizations",
          ];
          this.siteAdminChart1Data = [dataPoints];

          dataPoints = [];
          //total = this.adminDashboard.allLicenceTypeCompanies + this.adminDashboard.assetLicenceTypeCompanies + this.adminDashboard.incidentLicenceTypeCompanies;
          //dataPoints.push(total);
          dataPoints.push(this.adminDashboard.allLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.assetLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.incidentLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.projectLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.uatLicenceTypeCompanies);
          // this.admin1DoughnutChartLabels = [
          //   "Total",
          //   "All License Type",
          //   "Asset License Type",
          //   "Incident License Type",
          // ];
          this.siteAdminChart2Labels = [
            "License Type - All",
            "License Type - Asset",
            "License Type - Incident",
            "License Type - Project",
            "License Type - UAT",
          ];
          this.siteAdminChart2Data = [dataPoints];
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
  }

  loadOrgAdminData() {
    if (this.orgAdminDashboardVO.activeEmployees < 0) {
      this.service
        .orgAdminDashboard()
        .subscribe((data: OrgAdminDashboardVO) => {
          this.orgAdminDashboardVO = new OrgAdminDashboardVO(data);
          let dataPoints = [];
          dataPoints.push(this.orgAdminDashboardVO.activeEmployees);
          dataPoints.push(this.orgAdminDashboardVO.inHouseVendors);
          dataPoints.push(this.orgAdminDashboardVO.outSourcedVendors);

          dataPoints = [];
          this.orgAdminTilesData = [];
          for (let i in this.orgAdminDashboardVO.moduleWiseIncidents) {
            this.orgAdminTilesData[i] = {
              moduleId: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleId,
              name: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleName,
              value: this.orgAdminDashboardVO.moduleWiseIncidents[i].count || 0,
              backgroundColour: this.orgAdminChart1Colors[0].backgroundColor[i],
              detailDataParams: { moduleId: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleId, incidentType: "INCIDENT" }
            };
            dataPoints.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].count);
            this.orgAdminChart1Labels.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleName);
            if (this.hasOrgAdminChart1Data == false) {
              this.hasOrgAdminChart1Data = this.orgAdminDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.orgAdminChunkedTilesData = this.chunk(this.orgAdminTilesData, 5);
          this.orgAdminChart1Data = [dataPoints];

          dataPoints = [];
          this.orgAdmin1TilesData = [];
          for (let i in this.orgAdminDashboardVO.statusWiseIncidents) {
            this.orgAdmin1TilesData[i] = {
              name: this.orgAdminDashboardVO.statusWiseIncidents[i].status,
              value: this.orgAdminDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.orgAdminChart2Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                allClosed: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                incidentType: "INCIDENT"
                //status: this.getIncidentStatus(this.orgAdminDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.orgAdminDashboardVO.statusWiseIncidents[i].count);
            this.orgAdminChart2Labels.push(this.orgAdminDashboardVO.statusWiseIncidents[i].status);
            if (this.hasOrgAdminChart2Data == false) {
              this.hasOrgAdminChart2Data = this.orgAdminDashboardVO.statusWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.orgAdmin1ChunkedTilesData = this.chunk(this.orgAdmin1TilesData, 5);
          this.orgAdminChart2Data = [dataPoints];

          dataPoints = [];
          this.orgAdmin2TilesData = [];
          for (let i in this.orgAdminDashboardVO.moduleWiseAssetIncidents) {
            this.orgAdmin2TilesData[i] = {
              moduleId: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleId,
              name: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleName,
              value: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].count || 0,
              backgroundColour: this.orgAdminChart3Colors[0].backgroundColor[i],
              detailDataParams: { moduleId: this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleId, incidentType: "ASSET" }
            };
            dataPoints.push(this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].count);
            this.orgAdminChart3Labels.push(this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].moduleName);
            if (this.hasOrgAdminChart3Data == false) {
              this.hasOrgAdminChart3Data = this.orgAdminDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.orgAdmin2ChunkedTilesData = this.chunk(this.orgAdmin2TilesData, 5);
          this.orgAdminChart3Data = [dataPoints];

          dataPoints = [];
          this.orgAdmin3TilesData = [];
          for (let i in this.orgAdminDashboardVO.statusWiseAssetIncidents) {
            this.orgAdmin3TilesData[i] = {
              name: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status,
              value: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].count || 0,
              backgroundColour: this.orgAdminChart4Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status == "Open" ? true : false,
                allClosed: this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status == "Closed" ? true : false,
                incidentType: "ASSET"
                //status: this.getIncidentStatus(this.orgAdminDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.orgAdminDashboardVO.statusWiseAssetIncidents[i].count);
            this.orgAdminChart4Labels.push(this.orgAdminDashboardVO.statusWiseAssetIncidents[i].status);
            if (this.hasOrgAdminChart4Data == false) {
              this.hasOrgAdminChart4Data = this.orgAdminDashboardVO.statusWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.orgAdmin3ChunkedTilesData = this.chunk(this.orgAdmin3TilesData, 5);
          this.orgAdminChart4Data = [dataPoints];
          $(function () {
            $("#dataSets-org-admin").accordion({
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
  }

  loadUserData() {
    if (this.userDashboardVO.moduleWiseIncidents.length == 0) {
      this.service
        .userDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: UserDashboardVO) => {
          this.userDashboardVO = new UserDashboardVO(data);
          let dataPoints = [];
          this.userTilesData = [];
          for (let i in this.userDashboardVO.moduleWiseIncidents) {
            this.userTilesData[i] = {
              moduleId: this.userDashboardVO.moduleWiseIncidents[i].moduleId,
              name: this.userDashboardVO.moduleWiseIncidents[i].moduleName,
              value: this.userDashboardVO.moduleWiseIncidents[i].count || 0,
              backgroundColour: this.userChart1Colors[0].backgroundColor[i],
              detailDataParams: { moduleId: this.userDashboardVO.moduleWiseIncidents[i].moduleId, raisedUserId: this.loggedInUser.userId, incidentType: "INCIDENT" }
            };
            dataPoints.push(this.userDashboardVO.moduleWiseIncidents[i].count);
            this.userChart1Labels.push(this.userDashboardVO.moduleWiseIncidents[i].moduleName);
            if (this.hasUserChart1Data == false) {
              this.hasUserChart1Data = this.userDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.userChunkedTilesData = this.chunk(this.userTilesData, 5);
          this.userChart1Data = [dataPoints];

          dataPoints = [];
          this.user2TilesData = [];
          for (let i in this.userDashboardVO.moduleWiseAssetIncidents) {
            this.user2TilesData[i] = {
              moduleId: this.userDashboardVO.moduleWiseAssetIncidents[i].moduleId,
              name: this.userDashboardVO.moduleWiseAssetIncidents[i].moduleName,
              value: this.userDashboardVO.moduleWiseAssetIncidents[i].count || 0,
              backgroundColour: this.userChart3Colors[0].backgroundColor[i],
              detailDataParams: { moduleId: this.userDashboardVO.moduleWiseAssetIncidents[i].moduleId, raisedUserId: this.loggedInUser.userId, incidentType: "ASSET" }
            };
            dataPoints.push(this.userDashboardVO.moduleWiseAssetIncidents[i].count);
            this.userChart3Labels.push(this.userDashboardVO.moduleWiseAssetIncidents[i].moduleName);
            if (this.hasUserChart3Data == false) {
              this.hasUserChart3Data = this.userDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.user2ChunkedTilesData = this.chunk(this.user2TilesData, 5);
          this.userChart3Data = [dataPoints];

          dataPoints = [];
          this.user1TilesData = [];
          for (let i in this.userDashboardVO.statusWiseIncidents) {
            this.user1TilesData[i] = {
              name: this.userDashboardVO.statusWiseIncidents[i].status,
              value: this.userDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.userChart2Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.userDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                allClosed: this.userDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                incidentType: "INCIDENT",
                raisedUserId: this.loggedInUser.userId,
                //status: this.getIncidentStatus(this.userDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.userDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.userDashboardVO.statusWiseIncidents[i].count);
            this.userChart2Labels.push(this.userDashboardVO.statusWiseIncidents[i].status);
            if (this.hasUserChart2Data == false) {
              this.hasUserChart2Data = this.userDashboardVO.statusWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.user1ChunkedTilesData = this.chunk(this.user1TilesData, 5);
          this.userChart2Data = [dataPoints];

          dataPoints = [];
          this.user3TilesData = [];
          for (let i in this.userDashboardVO.statusWiseAssetIncidents) {
            this.user3TilesData[i] = {
              name: this.userDashboardVO.statusWiseAssetIncidents[i].status,
              value: this.userDashboardVO.statusWiseAssetIncidents[i].count || 0,
              backgroundColour: this.userChart4Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.userDashboardVO.statusWiseAssetIncidents[i].status == "Open" ? true : false,
                allClosed: this.userDashboardVO.statusWiseAssetIncidents[i].status == "Closed" ? true : false,
                incidentType: "ASSET",
                raisedUserId: this.loggedInUser.userId,
                //status: this.getIncidentStatus(this.userDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.userDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.userDashboardVO.statusWiseAssetIncidents[i].count);
            this.userChart4Labels.push(this.userDashboardVO.statusWiseAssetIncidents[i].status);
            if (this.hasUserChart4Data == false) {
              this.hasUserChart4Data = this.userDashboardVO.statusWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.user3ChunkedTilesData = this.chunk(this.user3TilesData, 5);
          this.userChart4Data = [dataPoints];
          $(function () {
            $("#dataSets-org-user").accordion({
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
  }

  loadAgentData() {
    if (this.agentDashboardVO.moduleWiseIncidents.length == 0) {
      this.service
        .agentDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: AgentDashboardVO) => {
          this.agentDashboardVO = new AgentDashboardVO(data);
          let dataPoints = [];
          this.agentTilesData = [];
          for (let i in this.agentDashboardVO.moduleWiseIncidents) {
            this.agentTilesData[i] = {
              moduleId: this.agentDashboardVO.moduleWiseIncidents[i].moduleId,
              name: this.agentDashboardVO.moduleWiseIncidents[i].moduleName,
              value: this.agentDashboardVO.moduleWiseIncidents[i].count || 0,
              backgroundColour: this.agentChart1Colors[0].backgroundColor[i],
              detailDataParams: {
                moduleId: this.agentDashboardVO.moduleWiseIncidents[i].moduleId,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
              }
            };
            dataPoints.push(this.agentDashboardVO.moduleWiseIncidents[i].count);
            this.agentChart1Labels.push(this.agentDashboardVO.moduleWiseIncidents[i].moduleName);
            if (this.hasAgentChart1Data == false) {
              this.hasAgentChart1Data = this.agentDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.agentChunkedTilesData = this.chunk(this.agentTilesData, 5);
          this.agentChart1Data = [dataPoints];

          dataPoints = [];
          this.agent3TilesData = [];
          for (let i in this.agentDashboardVO.moduleWiseAssetIncidents) {
            this.agent3TilesData[i] = {
              moduleId: this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleId,
              name: this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleName,
              value: this.agentDashboardVO.moduleWiseAssetIncidents[i].count || 0,
              backgroundColour: this.agentChart4Colors[0].backgroundColor[i],
              detailDataParams: {
                moduleId: this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleId,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
              }
            };
            dataPoints.push(this.agentDashboardVO.moduleWiseAssetIncidents[i].count);
            this.agentChart4Labels.push(this.agentDashboardVO.moduleWiseAssetIncidents[i].moduleName);
            if (this.hasAgentChart4Data == false) {
              this.hasAgentChart4Data = this.agentDashboardVO.moduleWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.agent3ChunkedTilesData = this.chunk(this.agent3TilesData, 5);
          this.agentChart4Data = [dataPoints];

          dataPoints = [];
          this.agent1TilesData = [];
          for (let i in this.agentDashboardVO.priorityWiseIncidents) {
            this.agent1TilesData[i] = {
              priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
              name: this.agentDashboardVO.priorityWiseIncidents[i].priority,
              value: this.agentDashboardVO.priorityWiseIncidents[i].count || 0,
              backgroundColour: this.agentChart2Colors[0].backgroundColor[i],
              detailDataParams: {
                priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
              }
            };
            dataPoints.push(this.agentDashboardVO.priorityWiseIncidents[i].count);
            this.agentChart2Labels.push(this.agentDashboardVO.priorityWiseIncidents[i].priority);
            if (this.hasAgentChart2Data == false) {
              this.hasAgentChart2Data = this.agentDashboardVO.priorityWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.agent1ChunkedTilesData = this.chunk(this.agent1TilesData, 5);
          this.agentChart2Data = [dataPoints];

          dataPoints = [];
          this.agent4TilesData = [];
          for (let i in this.agentDashboardVO.priorityWiseAssetIncidents) {
            this.agent4TilesData[i] = {
              priorityId: this.agentDashboardVO.priorityWiseAssetIncidents[i].priorityId,
              name: this.agentDashboardVO.priorityWiseAssetIncidents[i].priority,
              value: this.agentDashboardVO.priorityWiseAssetIncidents[i].count || 0,
              backgroundColour: this.agentChart5Colors[0].backgroundColor[i],
              detailDataParams: {
                priorityId: this.agentDashboardVO.priorityWiseAssetIncidents[i].priorityId,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
              }
            };
            dataPoints.push(this.agentDashboardVO.priorityWiseAssetIncidents[i].count);
            this.agentChart5Labels.push(this.agentDashboardVO.priorityWiseAssetIncidents[i].priority);
            if (this.hasAgentChart5Data == false) {
              this.hasAgentChart5Data = this.agentDashboardVO.priorityWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.agent4ChunkedTilesData = this.chunk(this.agent4TilesData, 5);
          this.agentChart5Data = [dataPoints];


          dataPoints = [];
          this.agent2TilesData = [];
          for (let i in this.agentDashboardVO.statusWiseIncidents) {
            this.agent2TilesData[i] = {
              name: this.agentDashboardVO.statusWiseIncidents[i].status,
              value: this.agentDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.agentChart3Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.agentDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                allClosed: this.agentDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                //status: this.getIncidentStatus(this.agentDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.agentDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.agentDashboardVO.statusWiseIncidents[i].count);
            this.agentChart3Labels.push(this.agentDashboardVO.statusWiseIncidents[i].status);
            if (this.hasAgentChart3Data == false) {
              this.hasAgentChart3Data = this.agentDashboardVO.statusWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.agent2ChunkedTilesData = this.chunk(this.agent2TilesData, 5);
          this.agentChart3Data = [dataPoints];

          dataPoints = [];
          this.agent5TilesData = [];
          for (let i in this.agentDashboardVO.statusWiseAssetIncidents) {
            this.agent5TilesData[i] = {
              name: this.agentDashboardVO.statusWiseAssetIncidents[i].status,
              value: this.agentDashboardVO.statusWiseAssetIncidents[i].count || 0,
              backgroundColour: this.agentChart6Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.agentDashboardVO.statusWiseAssetIncidents[i].status == "Open" ? true : false,
                allClosed: this.agentDashboardVO.statusWiseAssetIncidents[i].status == "Closed" ? true : false,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                //status: this.getIncidentStatus(this.agentDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.agentDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.agentDashboardVO.statusWiseAssetIncidents[i].count);
            this.agentChart6Labels.push(this.agentDashboardVO.statusWiseAssetIncidents[i].status);
            if (this.hasAgentChart6Data == false) {
              this.hasAgentChart6Data = this.agentDashboardVO.statusWiseAssetIncidents[i].count > 0 ? true : false;
            }
          }
          this.agent5ChunkedTilesData = this.chunk(this.agent5TilesData, 5);
          this.agentChart6Data = [dataPoints];

          $(function () {
            $("#dataSets-org-agent").accordion({
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
  }

  loadCategoryAdminData() {
    if (this.categoryAdminDashboardVO.moduleWiseIncidents.length == 0) {
      this.service
        .categoryAdminDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: CategoryAdminDashboardVO) => {
          this.categoryAdminDashboardVO = new CategoryAdminDashboardVO(data);
          let dataPoints = [];
          this.caTilesData = [];
          for (let i in this.categoryAdminDashboardVO.moduleWiseIncidents) {
            this.caTilesData[i] = {
              moduleId: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleId,
              name: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleName,
              value: this.categoryAdminDashboardVO.moduleWiseIncidents[i].count || 0,
              backgroundColour: this.categoryAdminChart1Colors[0].backgroundColor[i],
              detailDataParams: {
                moduleId: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleId,
                assignedUserId: "",
              }
            };
            dataPoints.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].count);
            this.categoryAdminChart1Labels.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleName);
            if (this.hasCategoryAdminChart1Data == false) {
              this.hasCategoryAdminChart1Data = this.categoryAdminDashboardVO.moduleWiseIncidents[i].count > 0 ? true : false;
            }
          }
          this.caChunkedTilesData = this.chunk(this.caTilesData, 5);
          this.categoryAdminChart1Data = [dataPoints];

          dataPoints = [];
          this.ca1TilesData = [];
          for (let i in this.categoryAdminDashboardVO.priorityWiseIncidents) {
            this.ca1TilesData[i] = {
              priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
              name: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority,
              value: this.categoryAdminDashboardVO.priorityWiseIncidents[i].count || 0,
              backgroundColour: this.categoryAdminChart2Colors[0].backgroundColor[i],
              detailDataParams: {
                priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
                assignedUserId: "",
              }
            };
            dataPoints.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].count);
            this.categoryAdminChart2Labels.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority);
          }
          this.ca1ChunkedTilesData = this.chunk(this.ca1TilesData, 5);
          this.categoryAdminChart2Data = [dataPoints];

          dataPoints = [];
          this.ca3TilesData = [];
          for (let i in this.categoryAdminDashboardVO.statusWiseIncidents) {
            this.ca3TilesData[i] = {
              name: this.categoryAdminDashboardVO.statusWiseIncidents[i].status,
              value: this.categoryAdminDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.categoryAdminChart4Colors[0].backgroundColor[i],
              detailDataParams: {
                allOpen: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Open" ? true : false,
                allClosed: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Closed" ? true : false,
                //status: this.getIncidentStatus(this.categoryAdminDashboardVO.statusWiseIncidents[i].status),
                //escalated1stLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                //escalated2ndLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                //isReopened: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].count);
            this.categoryAdminChart4Labels.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].status);
          }
          this.ca3ChunkedTilesData = this.chunk(this.ca3TilesData, 5);
          this.categoryAdminChart4Data = [dataPoints];

          dataPoints = [];
          dataPoints.push(this.categoryAdminDashboardVO.aging5);
          dataPoints.push(this.categoryAdminDashboardVO.aging10);
          dataPoints.push(this.categoryAdminDashboardVO.aging20);
          dataPoints.push(this.categoryAdminDashboardVO.aging30);
          dataPoints.push(this.categoryAdminDashboardVO.aging60);
          this.categoryAdminChart3Data = [dataPoints];
          this.categoryAdminChart3Labels = [
            " > 5 Days",
            " > 10 Days",
            " > 20 Days",
            " > 30 Days",
            " > 60 Days",
          ];
          $(function () {
            $("#dataSets-org-category-admin").accordion({
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
  }

  chunk(arr, size) {
    var newArr = [];
    for (var i = 0; i < arr.length; i += size) {
      newArr.push(arr.slice(i, i + size));
    }
    return newArr;
  }

  hasUserRole() {
    for (let k in this.roles) {
      if (this.roles[k].match(/.*_USER_.*/)) {
        return true
      }
    }
    return false;
  }

  hasAgentRole() {
    for (let k in this.roles) {
      if (this.roles[k].match(/.*_AGENT_.*/)) {
        return true
      }
    }
    return false;
  }

  hasCategoryAdminRole() {
    for (let k in this.roles) {
      if (this.roles[k].match(/.*_CATEGORY_ADMIN.*/)) {
        return true
      }
    }
    return false;
  }

  chartHovered(e: any) {
    //this.getChartSegmentData(e);
  }

  siteAdminChart1(e: any) {
    if (this.getChartSegmentData(e) === "Total Organizations") {
      this.viewSiteAdmin({ "status": "ALL" });
    } else if (this.getChartSegmentData(e) === "Active Organizations") {
      this.viewSiteAdmin({ "status": "ACTIVE" })
    } else if (this.getChartSegmentData(e) === "Inactive Organizations") {
      this.viewSiteAdmin({ "status": "INACTIVE" })
    } else {
      this.viewSiteAdmin({ "status": "ALL" });
    }
  }

  siteAdminChart2(e: any) {
    if (this.getChartSegmentData(e) === "Total") {
      this.viewSiteAdmin({ "status": "ALL" });
    } else if (this.getChartSegmentData(e) === "License Type - All") {
      this.viewSiteAdmin({ "status": "ALL", })
    } else if (this.getChartSegmentData(e) === "License Type - Asset") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "ASSET" })
    } else if (this.getChartSegmentData(e) === "License Type - Incident") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "INCIDENT" })
    } else if (this.getChartSegmentData(e) === "License Type - Project") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "PROJECT" })
    } else if (this.getChartSegmentData(e) === "License Type - UAT") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "UAT" })
    } else {
      this.viewSiteAdmin({ "status": "ALL" });
    }
  }

  userChart1(e: any, type: string = "user") {
    for (let k in this.userTilesData) {
      if (this.userTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.userTilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  userChart3(e: any, type: string = "user") {
    for (let k in this.user2TilesData) {
      if (this.user2TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.user2TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }

  userChart2(e: any, type: string = "user") {
    for (let k in this.user1TilesData) {
      if (this.user1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.user1TilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  userChart4(e: any, type: string = "user") {
    for (let k in this.user3TilesData) {
      if (this.user3TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.user3TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }

  agentChart2(e: any, type: string = "agent") {
    for (let k in this.agentTilesData) {
      if (this.agentTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agentTilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  agentChart4(e: any, type: string = "agent") {
    for (let k in this.agent3TilesData) {
      if (this.agent3TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent3TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }

  agentChart3(e: any, type: string = "agent") {
    for (let k in this.agent2TilesData) {
      if (this.agent2TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent2TilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  agentChart6(e: any, type: string = "agent") {
    for (let k in this.agent5TilesData) {
      if (this.agent5TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent5TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }

  agentChart1(e: any, type: string = "agent") {
    for (let k in this.agent1TilesData) {
      if (this.agent1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent1TilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  agentChart5(e: any, type: string = "agent") {
    for (let k in this.agent4TilesData) {
      if (this.agent4TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent4TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }


  caChart3(e: any, type: string = "user") {
    for (let k in this.caTilesData) {
      if (this.caTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.caTilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  caChart5(e: any, type: string = "user") {
    for (let k in this.ca4TilesData) {
      if (this.ca4TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.ca4TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }

  caChart4(e: any, type: string = "user") {
    for (let k in this.ca3TilesData) {
      if (this.ca3TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.ca3TilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  caChart2(e: any, type: string = "user") {
    for (let k in this.ca1TilesData) {
      if (this.ca1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.ca1TilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  caChart1(e: any, type: string = "user") {
    if (this.getChartSegmentData(e) == " > 5 Days") {
      this.viewIncident({ agingFilter: ">=5" }, type);
    } else if (this.getChartSegmentData(e) == " > 10 Days") {
      this.viewIncident({ agingFilter: ">=10" }, type);
    } else if (this.getChartSegmentData(e) == " > 20 Days") {
      this.viewIncident({ agingFilter: ">=20" }, type);
    } else if (this.getChartSegmentData(e) == " > 30 Days") {
      this.viewIncident({ agingFilter: ">=30" }, type);
    } else if (this.getChartSegmentData(e) == " > 60 Days") {
      this.viewIncident({ agingFilter: ">60" }, type);
    }
  }

  orgAdminChart1(e: any, type: string = "user") {
    for (let k in this.orgAdminTilesData) {
      if (this.orgAdminTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.orgAdminTilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  orgAdminChart2(e: any, type: string = "user") {
    for (let k in this.orgAdmin1TilesData) {
      if (this.orgAdmin1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.orgAdmin1TilesData[k].detailDataParams, type);
      } else {
        continue;
      }
    }
  }

  orgAdminChart3(e: any, type: string = "user") {
    for (let k in this.orgAdmin2TilesData) {
      if (this.orgAdmin2TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.orgAdmin2TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
  }

  orgAdminChart4(e: any, type: string = "user") {
    for (let k in this.orgAdmin3TilesData) {
      if (this.orgAdmin3TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.orgAdmin3TilesData[k].detailDataParams, type, "ASSET");
      } else {
        continue;
      }
    }
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

  viewIncident(element: any, type: string, incidentType = 'INCIDENT') {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    element.incidentType = incidentType;
    const initialState = {
      params: element,
      type: type
    };
    this.modalRef = this.modalService.show(ViewIncidentDetails,
      Object.assign({}, config, { initialState })
    );
  }

  viewSiteAdmin(element: any) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: element
    };
    this.modalRef = this.modalService.show(ViewAppAdminDashboardDetails,
      Object.assign({}, config, { initialState })
    );
  }

  viewOrgAdminUserDetail(element: any) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: element
    };
    this.modalRef = this.modalService.show(ViewOrgAdminDashboardUserDetails,
      Object.assign({}, config, { initialState })
    );
  }

  viewOrgAdminVendorDetail(element: any) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: element
    };
    this.modalRef = this.modalService.show(ViewOrgAdminDashboardVendorDetails,
      Object.assign({}, config, { initialState })
    );
  }

  viewUser(element: any) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: element
    };
    this.modalRef = this.modalService.show(ViewUserDashboardDetails,
      Object.assign({}, config, { initialState })
    );
  }

}