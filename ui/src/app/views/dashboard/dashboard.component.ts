import { Component, OnInit, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { DashboardService } from '../../service/DashboardService';
import { AdminDashboardVO } from '../../model/AdminDashboardVO';
import { FormBuilder } from '@angular/forms';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { Label, MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';
import { OrgAdminDashboardVO } from '../../model/OrgAdminDashboardVO';
import { UserDashboardVO } from '../../model/UserDashboardVO';
import { IncidentDataSource } from '../../service/datasource/IncidentDataSource';
import { IncidentService } from '../../service/IncidentService';
import { AgentDashboardVO } from '../../model/AgentDashboardVO';
import { CategoryAdminDashboardVO } from '../../model/CategoryAdminDashboardVO';
import { ViewIncidentDetails } from './modal/ViewIncidentDetails';
import { ViewAppAdminDashboardDetails } from './modal/ViewAppAdminDashboardDetails';
import { ViewOrgAdminDashboardUserDetails } from './modal/ViewOrgAdminDashboardUserDetails';
import { ViewOrgAdminDashboardVendorDetails } from './modal/ViewOrgAdminDashboardVendorDetails';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';

@Component({
  templateUrl: 'dashboard.component.html'
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
  userTilesData: any[] = [];
  userChunkedTilesData: any[] = [];
  user1TilesData: any[] = [];
  user1ChunkedTilesData: any[] = [];
  orgAdminTilesData: any[] = [];
  orgAdminChunkedTilesData: any[] = [];
  orgAdmin1TilesData: any[] = [];
  orgAdmin1ChunkedTilesData: any[] = [];
  agentTilesData: any[] = [];
  agentChunkedTilesData: any[] = [];
  agent1TilesData: any[] = [];
  agent1ChunkedTilesData: any[] = [];
  agent2TilesData: any[] = [];
  agent2ChunkedTilesData: any[] = [];
  caTilesData: any[] = [];
  caChunkedTilesData: any[] = [];
  ca1TilesData: any[] = [];
  ca1ChunkedTilesData: any[] = [];
  ca2TilesData: any[] = [];
  ca2ChunkedTilesData: any[] = [];
  ca3TilesData: any[] = [];
  ca3ChunkedTilesData: any[] = [];

  public adminDoughnutChartLabels: Label[] = [];
  public adminDoughnutChartData: MultiDataSet = [];
  public adminDoughnutChartType: ChartType = 'pie';
  public adminChartColors: any[] = [{ backgroundColor: ["#63C2DE", "#FAC008", "#7048C1"] }];
  public adminDoughnutChartOptions: any = { responsive: true, boxWidth: 1, };

  public admin1DoughnutChartLabels: Label[] = [];
  public admin1DoughnutChartData: MultiDataSet = [];
  public admin1DoughnutChartType: ChartType = 'pie';
  //public admin1ChartColors: any[] = [{ backgroundColor: ["#adff2f", "#EE6A6C", "#5FBD74", "#3B5998",] }];
  public admin1ChartColors: any[] = [{ backgroundColor: ["#EE6A6C", "#5FBD74", "#3B5998",] }];
  public admin1DoughnutChartOptions: any = { responsive: true, boxWidth: 1, };

  public orgAdmin1DoughnutChartLabels: Label[] = [];
  public orgAdmin1DoughnutChartData: MultiDataSet = [];
  public orgAdmin1DoughnutChartType: ChartType = 'pie';
  public orgAdmin1ChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
  }];
  public orgAdmin1DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public orgAdmin2DoughnutChartLabels: Label[] = [];
  public orgAdmin2DoughnutChartData: MultiDataSet = [];
  public orgAdmin2DoughnutChartType: ChartType = 'pie';
  public orgAdmin2ChartColors: any[] = [{
    backgroundColor: ["#42A3B8", "#adff2f", "#7048C1", "#63CA96", "#f86c6b", "#FAC008", "#3B5998", "#E9518B", "#63C2DE", "#ffc107", "#F8CB00",]
  }];
  public orgAdmin2DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public userDoughnutChartLabels: Label[] = [];
  public userDoughnutChartData: MultiDataSet = [];
  public userDoughnutChartType: ChartType = 'pie';
  public userChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
  }];
  public userDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public user1DoughnutChartLabels: Label[] = [];
  public user1DoughnutChartData: MultiDataSet = [];
  public user1DoughnutChartType: ChartType = 'pie';
  public user1ChartColors: any[] = [{
    backgroundColor: ["#42A3B8", "#adff2f", "#7048C1", "#63CA96", "#f86c6b", "#FAC008", "#3B5998", "#E9518B", "#63C2DE", "#ffc107", "#F8CB00",]
  }];
  public user1DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public agentDoughnutChartLabels: Label[] = [];
  public agentDoughnutChartData: MultiDataSet = [];
  public agentDoughnutChartType: ChartType = 'pie';
  public agentChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
  }];
  public agentDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public agent1DoughnutChartLabels: Label[] = [];
  public agent1DoughnutChartData: MultiDataSet = [];
  public agent1DoughnutChartType: ChartType = 'pie';
  public agent1ChartColors: any[] = [{
    backgroundColor: ["#3DA8D8", "#E9518B", "#FAC008", "#7048C1", "#fd72e6", "#ffc107", "#17a2b8", "#20c997", "#ffc107", "#f86c6b", "#6FC8CE",]
  }];
  public agent1DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public agent2DoughnutChartLabels: Label[] = [];
  public agent2DoughnutChartData: MultiDataSet = [];
  public agent2DoughnutChartType: ChartType = 'pie';
  public agent2ChartColors: any[] = [{
    backgroundColor: ["#42A3B8", "#adff2f", "#7048C1", "#63CA96", "#f86c6b", "#FAC008", "#3B5998", "#E9518B", "#63C2DE", "#ffc107", "#F8CB00",]
  }];
  public agent2DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public caDoughnutChartLabels: Label[] = [];
  public caDoughnutChartData: MultiDataSet = [];
  public caDoughnutChartType: ChartType = 'pie';
  public caChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#adff2f", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
  }];
  public caDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public ca1DoughnutChartLabels: Label[] = [];
  public ca1DoughnutChartData: MultiDataSet = [];
  public ca1DoughnutChartType: ChartType = 'pie';
  public ca1ChartColors: any[] = [{
    backgroundColor: ["#3DA8D8", "#E9518B", "#FAC008", "#7048C1", "#fd72e6", "#ffc107", "#17a2b8", "#20c997", "#ffc107", "#f86c6b", "#6FC8CE",]
  }];
  public ca1DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public ca2DoughnutChartLabels: Label[] = [];
  public ca2DoughnutChartData: MultiDataSet = [];
  public ca2DoughnutChartType: ChartType = 'pie';
  public ca2ChartColors: any[] = [{
    backgroundColor: ["#F8CB0F", "#E9528D", "#5FBE76", "#7249C2", "#3EA9D8", "#ffc107", "#17a2b8", "#20c997", "#ffc107", "#f86c6b", "#6FC8CE",]
  }];
  public ca2DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public ca3DoughnutChartLabels: Label[] = [];
  public ca3DoughnutChartData: MultiDataSet = [];
  public ca3DoughnutChartType: ChartType = 'pie';
  public ca3ChartColors: any[] = [{
    backgroundColor: ["#42A3B8", "#adff2f", "#7048C1", "#63CA96", "#f86c6b", "#FAC008", "#3B5998", "#E9518B", "#63C2DE", "#ffc107", "#F8CB00",]
  }];
  public ca3DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

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
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.roles = this.loggedInUser.roles;
    //console.log(JSON.stringify(this.loggedInUser.modulePermissions));
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
          this.adminDoughnutChartLabels = [
            "Total",
            "Active",
            "Inactive",
          ];
          this.adminDoughnutChartData = [dataPoints];
          this.adminDoughnutChartType = 'pie';

          dataPoints = [];
          //total = this.adminDashboard.allLicenceTypeCompanies + this.adminDashboard.assetLicenceTypeCompanies + this.adminDashboard.incidentLicenceTypeCompanies;
          //dataPoints.push(total);
          dataPoints.push(this.adminDashboard.allLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.assetLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.incidentLicenceTypeCompanies);
          // this.admin1DoughnutChartLabels = [
          //   "Total",
          //   "All License Type",
          //   "Asset License Type",
          //   "Incident License Type",
          // ];
          this.admin1DoughnutChartLabels = [
            "All License Type",
            "Asset License Type",
            "Incident License Type",
          ];
          this.admin1DoughnutChartData = [dataPoints];
          this.admin1DoughnutChartType = 'pie';
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
                backgroundColour: this.orgAdmin1ChartColors[0].backgroundColor[i],
                detailDataParams: { moduleId: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleId }
              };
              dataPoints.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].count);
              this.orgAdmin1DoughnutChartLabels.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleName);
            }
            this.orgAdminChunkedTilesData = this.chunk(this.orgAdminTilesData, 3);
            this.orgAdmin1DoughnutChartData = [dataPoints];
            this.orgAdmin1DoughnutChartType = 'pie';
            dataPoints = [];
            this.orgAdmin1TilesData = [];
            for (let i in this.orgAdminDashboardVO.statusWiseIncidents) {
              this.orgAdmin1TilesData[i] = {
                name: this.orgAdminDashboardVO.statusWiseIncidents[i].status,
                value: this.orgAdminDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.orgAdmin2ChartColors[0].backgroundColor[i],
                detailDataParams: {
                  status: this.getIncidentStatus(this.orgAdminDashboardVO.statusWiseIncidents[i].status),
                  escalated1stLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  escalated2ndLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  isReopened: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.orgAdminDashboardVO.statusWiseIncidents[i].count);
              this.orgAdmin2DoughnutChartLabels.push(this.orgAdminDashboardVO.statusWiseIncidents[i].status);
            }
            this.orgAdmin1ChunkedTilesData = this.chunk(this.orgAdmin1TilesData, 3);
            this.orgAdmin2DoughnutChartData = [dataPoints];
            this.orgAdmin2DoughnutChartType = 'pie';
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
                backgroundColour: this.userChartColors[0].backgroundColor[i],
                detailDataParams: { moduleId: this.userDashboardVO.moduleWiseIncidents[i].moduleId, raisedUserId: this.loggedInUser.userId }
              };
              dataPoints.push(this.userDashboardVO.moduleWiseIncidents[i].count);
              this.userDoughnutChartLabels.push(this.userDashboardVO.moduleWiseIncidents[i].moduleName);
            }
            this.userChunkedTilesData = this.chunk(this.userTilesData, 3);
            this.userDoughnutChartData = [dataPoints];
            this.userDoughnutChartType = 'pie';
            dataPoints = [];
            this.user1TilesData = [];
            for (let i in this.userDashboardVO.statusWiseIncidents) {
              this.user1TilesData[i] = {
                name: this.userDashboardVO.statusWiseIncidents[i].status,
                value: this.userDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.user1ChartColors[0].backgroundColor[i],
                detailDataParams: {
                  status: this.getIncidentStatus(this.userDashboardVO.statusWiseIncidents[i].status),
                  escalated1stLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  escalated2ndLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  isReopened: this.userDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.userDashboardVO.statusWiseIncidents[i].count);
              this.user1DoughnutChartLabels.push(this.userDashboardVO.statusWiseIncidents[i].status);
            }
            this.user1ChunkedTilesData = this.chunk(this.user1TilesData, 3);
            this.user1DoughnutChartData = [dataPoints];
            this.user1DoughnutChartType = 'pie';
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
                backgroundColour: this.userChartColors[0].backgroundColor[i],
                detailDataParams: {
                  moduleId: this.agentDashboardVO.moduleWiseIncidents[i].moduleId,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                }
              };
              dataPoints.push(this.agentDashboardVO.moduleWiseIncidents[i].count);
              this.agentDoughnutChartLabels.push(this.agentDashboardVO.moduleWiseIncidents[i].moduleName);
            }
            this.agentChunkedTilesData = this.chunk(this.agentTilesData, 3);
            this.agentDoughnutChartData = [dataPoints];
            this.agentDoughnutChartType = 'pie';
            dataPoints = [];
            this.agent1TilesData = [];
            for (let i in this.agentDashboardVO.priorityWiseIncidents) {
              this.agent1TilesData[i] = {
                priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
                name: this.agentDashboardVO.priorityWiseIncidents[i].priority,
                value: this.agentDashboardVO.priorityWiseIncidents[i].count || 0,
                backgroundColour: this.agent1ChartColors[0].backgroundColor[i],
                detailDataParams: {
                  priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
                  assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
                }
              };
              dataPoints.push(this.agentDashboardVO.priorityWiseIncidents[i].count);
              this.agent1DoughnutChartLabels.push(this.agentDashboardVO.priorityWiseIncidents[i].priority);
            }
            this.agent1ChunkedTilesData = this.chunk(this.agent1TilesData, 3);
            this.agent1DoughnutChartData = [dataPoints];
            this.agent1DoughnutChartType = 'pie';
            dataPoints = [];
            this.agent2TilesData = [];
            for (let i in this.agentDashboardVO.statusWiseIncidents) {
              this.agent2TilesData[i] = {
                name: this.agentDashboardVO.statusWiseIncidents[i].status,
                value: this.agentDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.agent2ChartColors[0].backgroundColor[i],
                detailDataParams: {
                  status: this.getIncidentStatus(this.agentDashboardVO.statusWiseIncidents[i].status),
                  escalated1stLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  escalated2ndLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  isReopened: this.agentDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.agentDashboardVO.statusWiseIncidents[i].count);
              this.agent2DoughnutChartLabels.push(this.agentDashboardVO.statusWiseIncidents[i].status);
            }
            this.agent2ChunkedTilesData = this.chunk(this.agent2TilesData, 3);
            this.agent2DoughnutChartData = [dataPoints];
            this.agent2DoughnutChartType = 'pie';
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
                backgroundColour: this.caChartColors[0].backgroundColor[i],
                detailDataParams: {
                  moduleId: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleId,
                  assignedUserId: "",
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].count);
              this.caDoughnutChartLabels.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleName);
            }
            this.caChunkedTilesData = this.chunk(this.caTilesData, 3);
            this.caDoughnutChartData = [dataPoints];
            this.caDoughnutChartType = 'pie';
            dataPoints = [];
            this.ca1TilesData = [];
            for (let i in this.categoryAdminDashboardVO.priorityWiseIncidents) {
              this.ca1TilesData[i] = {
                priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
                name: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority,
                value: this.categoryAdminDashboardVO.priorityWiseIncidents[i].count || 0,
                backgroundColour: this.ca1ChartColors[0].backgroundColor[i],
                detailDataParams: {
                  priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
                  assignedUserId: "",
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].count);
              this.ca1DoughnutChartLabels.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority);
            }
            this.ca1ChunkedTilesData = this.chunk(this.ca1TilesData, 3);
            this.ca1DoughnutChartData = [dataPoints];
            this.ca1DoughnutChartType = 'pie';
            dataPoints = [];
            this.ca3TilesData = [];
            for (let i in this.categoryAdminDashboardVO.statusWiseIncidents) {
              this.ca3TilesData[i] = {
                name: this.categoryAdminDashboardVO.statusWiseIncidents[i].status,
                value: this.categoryAdminDashboardVO.statusWiseIncidents[i].count || 0,
                backgroundColour: this.ca3ChartColors[0].backgroundColor[i],
                detailDataParams: {
                  status: this.getIncidentStatus(this.categoryAdminDashboardVO.statusWiseIncidents[i].status),
                  escalated1stLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                  escalated2ndLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                  isReopened: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
                }
              };
              dataPoints.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].count);
              this.ca3DoughnutChartLabels.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].status);
            }
            this.ca3ChunkedTilesData = this.chunk(this.ca3TilesData, 3);
            this.ca3DoughnutChartData = [dataPoints];
            this.ca3DoughnutChartType = 'pie';
            dataPoints = [];
            dataPoints.push(this.categoryAdminDashboardVO.aging5);
            dataPoints.push(this.categoryAdminDashboardVO.aging10);
            dataPoints.push(this.categoryAdminDashboardVO.aging20);
            dataPoints.push(this.categoryAdminDashboardVO.aging30);
            dataPoints.push(this.categoryAdminDashboardVO.aging60);
            this.ca2DoughnutChartData = [dataPoints];
            this.ca2DoughnutChartLabels = [
              " > 5 Days",
              " > 10 Days",
              " > 20 Days",
              " > 30 Days",
              " > 60 Days",
            ];
            this.ca2DoughnutChartType = 'pie';
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
          this.adminDoughnutChartLabels = [
            "Total",
            "Active",
            "Inactive",
          ];
          this.adminDoughnutChartData = [dataPoints];
          this.adminDoughnutChartType = 'pie';

          dataPoints = [];
          //total = this.adminDashboard.allLicenceTypeCompanies + this.adminDashboard.assetLicenceTypeCompanies + this.adminDashboard.incidentLicenceTypeCompanies;
          //dataPoints.push(total);
          dataPoints.push(this.adminDashboard.allLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.assetLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.incidentLicenceTypeCompanies);
          // this.admin1DoughnutChartLabels = [
          //   "Total",
          //   "All License Type",
          //   "Asset License Type",
          //   "Incident License Type",
          // ];
          this.admin1DoughnutChartLabels = [
            "All License Type",
            "Asset License Type",
            "Incident License Type",
          ];
          this.admin1DoughnutChartData = [dataPoints];
          this.admin1DoughnutChartType = 'pie';
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
              backgroundColour: this.orgAdmin1ChartColors[0].backgroundColor[i],
              detailDataParams: { moduleId: this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleId }
            };
            dataPoints.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].count);
            this.orgAdmin1DoughnutChartLabels.push(this.orgAdminDashboardVO.moduleWiseIncidents[i].moduleName);
          }
          this.orgAdminChunkedTilesData = this.chunk(this.orgAdminTilesData, 3);
          this.orgAdmin1DoughnutChartData = [dataPoints];
          this.orgAdmin1DoughnutChartType = 'pie';
          dataPoints = [];
          this.orgAdmin1TilesData = [];
          for (let i in this.orgAdminDashboardVO.statusWiseIncidents) {
            this.orgAdmin1TilesData[i] = {
              name: this.orgAdminDashboardVO.statusWiseIncidents[i].status,
              value: this.orgAdminDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.orgAdmin2ChartColors[0].backgroundColor[i],
              detailDataParams: {
                status: this.getIncidentStatus(this.orgAdminDashboardVO.statusWiseIncidents[i].status),
                escalated1stLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                escalated2ndLevel: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                isReopened: this.orgAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.orgAdminDashboardVO.statusWiseIncidents[i].count);
            this.orgAdmin2DoughnutChartLabels.push(this.orgAdminDashboardVO.statusWiseIncidents[i].status);
          }
          this.orgAdmin1ChunkedTilesData = this.chunk(this.orgAdmin1TilesData, 3);
          this.orgAdmin2DoughnutChartData = [dataPoints];
          this.orgAdmin2DoughnutChartType = 'pie';
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
              backgroundColour: this.userChartColors[0].backgroundColor[i],
              detailDataParams: { moduleId: this.userDashboardVO.moduleWiseIncidents[i].moduleId, raisedUserId: this.loggedInUser.userId }
            };
            dataPoints.push(this.userDashboardVO.moduleWiseIncidents[i].count);
            this.userDoughnutChartLabels.push(this.userDashboardVO.moduleWiseIncidents[i].moduleName);
          }
          this.userChunkedTilesData = this.chunk(this.userTilesData, 3);
          this.userDoughnutChartData = [dataPoints];
          this.userDoughnutChartType = 'pie';
          dataPoints = [];
          this.user1TilesData = [];
          for (let i in this.userDashboardVO.statusWiseIncidents) {
            this.user1TilesData[i] = {
              name: this.userDashboardVO.statusWiseIncidents[i].status,
              value: this.userDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.user1ChartColors[0].backgroundColor[i],
              detailDataParams: {
                status: this.getIncidentStatus(this.userDashboardVO.statusWiseIncidents[i].status),
                escalated1stLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                escalated2ndLevel: this.userDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                isReopened: this.userDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.userDashboardVO.statusWiseIncidents[i].count);
            this.user1DoughnutChartLabels.push(this.userDashboardVO.statusWiseIncidents[i].status);
          }
          this.user1ChunkedTilesData = this.chunk(this.user1TilesData, 3);
          this.user1DoughnutChartData = [dataPoints];
          this.user1DoughnutChartType = 'pie';
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
              backgroundColour: this.userChartColors[0].backgroundColor[i],
              detailDataParams: {
                moduleId: this.agentDashboardVO.moduleWiseIncidents[i].moduleId,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
              }
            };
            dataPoints.push(this.agentDashboardVO.moduleWiseIncidents[i].count);
            this.agentDoughnutChartLabels.push(this.agentDashboardVO.moduleWiseIncidents[i].moduleName);
          }
          this.agentChunkedTilesData = this.chunk(this.agentTilesData, 3);
          this.agentDoughnutChartData = [dataPoints];
          this.agentDoughnutChartType = 'pie';
          dataPoints = [];
          this.agent1TilesData = [];
          for (let i in this.agentDashboardVO.priorityWiseIncidents) {
            this.agent1TilesData[i] = {
              priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
              name: this.agentDashboardVO.priorityWiseIncidents[i].priority,
              value: this.agentDashboardVO.priorityWiseIncidents[i].count || 0,
              backgroundColour: this.agent1ChartColors[0].backgroundColor[i],
              detailDataParams: {
                priorityId: this.agentDashboardVO.priorityWiseIncidents[i].priorityId,
                assignedUserId: (this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_LEAD') || this.loggedInUserService.hasRole('ORG_INCIDENT_AGENT_MANAGER')) ? "" : this.loggedInUser.userId,
              }
            };
            dataPoints.push(this.agentDashboardVO.priorityWiseIncidents[i].count);
            this.agent1DoughnutChartLabels.push(this.agentDashboardVO.priorityWiseIncidents[i].priority);
          }
          this.agent1ChunkedTilesData = this.chunk(this.agent1TilesData, 3);
          this.agent1DoughnutChartData = [dataPoints];
          this.agent1DoughnutChartType = 'pie';
          dataPoints = [];
          this.agent2TilesData = [];
          for (let i in this.agentDashboardVO.statusWiseIncidents) {
            this.agent2TilesData[i] = {
              name: this.agentDashboardVO.statusWiseIncidents[i].status,
              value: this.agentDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.agent2ChartColors[0].backgroundColor[i],
              detailDataParams: {
                status: this.getIncidentStatus(this.agentDashboardVO.statusWiseIncidents[i].status),
                escalated1stLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                escalated2ndLevel: this.agentDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                isReopened: this.agentDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.agentDashboardVO.statusWiseIncidents[i].count);
            this.agent2DoughnutChartLabels.push(this.agentDashboardVO.statusWiseIncidents[i].status);
          }
          this.agent2ChunkedTilesData = this.chunk(this.agent2TilesData, 3);
          this.agent2DoughnutChartData = [dataPoints];
          this.agent2DoughnutChartType = 'pie';
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
              backgroundColour: this.caChartColors[0].backgroundColor[i],
              detailDataParams: {
                moduleId: this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleId,
                assignedUserId: "",
              }
            };
            dataPoints.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].count);
            this.caDoughnutChartLabels.push(this.categoryAdminDashboardVO.moduleWiseIncidents[i].moduleName);
          }
          this.caChunkedTilesData = this.chunk(this.caTilesData, 3);
          this.caDoughnutChartData = [dataPoints];
          this.caDoughnutChartType = 'pie';
          dataPoints = [];
          this.ca1TilesData = [];
          for (let i in this.categoryAdminDashboardVO.priorityWiseIncidents) {
            this.ca1TilesData[i] = {
              priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
              name: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority,
              value: this.categoryAdminDashboardVO.priorityWiseIncidents[i].count || 0,
              backgroundColour: this.ca1ChartColors[0].backgroundColor[i],
              detailDataParams: {
                priorityId: this.categoryAdminDashboardVO.priorityWiseIncidents[i].priorityId,
                assignedUserId: "",
              }
            };
            dataPoints.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].count);
            this.ca1DoughnutChartLabels.push(this.categoryAdminDashboardVO.priorityWiseIncidents[i].priority);
          }
          this.ca1ChunkedTilesData = this.chunk(this.ca1TilesData, 3);
          this.ca1DoughnutChartData = [dataPoints];
          this.ca1DoughnutChartType = 'pie';
          dataPoints = [];
          this.ca3TilesData = [];
          for (let i in this.categoryAdminDashboardVO.statusWiseIncidents) {
            this.ca3TilesData[i] = {
              name: this.categoryAdminDashboardVO.statusWiseIncidents[i].status,
              value: this.categoryAdminDashboardVO.statusWiseIncidents[i].count || 0,
              backgroundColour: this.ca3ChartColors[0].backgroundColor[i],
              detailDataParams: {
                status: this.getIncidentStatus(this.categoryAdminDashboardVO.statusWiseIncidents[i].status),
                escalated1stLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 1st Level" ? true : false,
                escalated2ndLevel: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Escalated 2nd Level" ? true : false,
                isReopened: this.categoryAdminDashboardVO.statusWiseIncidents[i].status == "Reopened" ? true : false
              }
            };
            dataPoints.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].count);
            this.ca3DoughnutChartLabels.push(this.categoryAdminDashboardVO.statusWiseIncidents[i].status);
          }
          this.ca3ChunkedTilesData = this.chunk(this.ca3TilesData, 3);
          this.ca3DoughnutChartData = [dataPoints];
          this.ca3DoughnutChartType = 'pie';
          dataPoints = [];
          dataPoints.push(this.categoryAdminDashboardVO.aging5);
          dataPoints.push(this.categoryAdminDashboardVO.aging10);
          dataPoints.push(this.categoryAdminDashboardVO.aging20);
          dataPoints.push(this.categoryAdminDashboardVO.aging30);
          dataPoints.push(this.categoryAdminDashboardVO.aging60);
          this.ca2DoughnutChartData = [dataPoints];
          this.ca2DoughnutChartLabels = [
            " > 5 Days",
            " > 10 Days",
            " > 20 Days",
            " > 30 Days",
            " > 60 Days",
          ];
          this.ca2DoughnutChartType = 'pie';
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

  appAdminChart1SegmentClicked(e: any) {
    if (this.getChartSegmentData(e) === "Total") {
      this.viewSiteAdmin({ "status": "ALL" });
    } else if (this.getChartSegmentData(e) === "Active") {
      this.viewSiteAdmin({ "status": "ACTIVE" })
    } else if (this.getChartSegmentData(e) === "Inactive") {
      this.viewSiteAdmin({ "status": "INACTIVE" })
    } else {
      this.viewSiteAdmin({ "status": "ALL" });
    }
  }

  appAdminChart2SegmentClicked(e: any) {
    if (this.getChartSegmentData(e) === "Total") {
      this.viewSiteAdmin({ "status": "ALL" });
    } else if (this.getChartSegmentData(e) === "All License Type") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "ALL" })
    } else if (this.getChartSegmentData(e) === "Asset License Type") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "ASSET" })
    } else if (this.getChartSegmentData(e) === "Incident License Type") {
      this.viewSiteAdmin({ "status": "ALL", "licenseType": "INCIDENT" })
    } else {
      this.viewSiteAdmin({ "status": "ALL" });
    }
  }

  userIncidentSegmentClicked(e: any) {
    for (let k in this.userTilesData) {
      if (this.userTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.userTilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  userIncident1SegmentClicked(e: any) {
    for (let k in this.user1TilesData) {
      if (this.user1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.user1TilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  agentIncidentSegmentClicked(e: any) {
    for (let k in this.agentTilesData) {
      if (this.agentTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agentTilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  agent1IncidentSegmentClicked(e: any) {
    for (let k in this.agent2TilesData) {
      if (this.agent2TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent2TilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  agentIncidentPrioritySegmentClicked(e: any) {
    for (let k in this.agent1TilesData) {
      if (this.agent1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.agent1TilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  caIncidentSegmentClicked(e: any) {
    for (let k in this.caTilesData) {
      if (this.caTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.caTilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  ca3IncidentSegmentClicked(e: any) {
    for (let k in this.ca3TilesData) {
      if (this.ca3TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.ca3TilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  caIncidentPrioritySegmentClicked(e: any) {
    for (let k in this.ca1TilesData) {
      if (this.ca1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.ca1TilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  caIncidentAgingSegmentClicked(e: any) {
    if (this.getChartSegmentData(e) == " > 5 Days") {
      this.viewIncident({ agingFilter: ">=5" });
    } else if (this.getChartSegmentData(e) == " > 10 Days") {
      this.viewIncident({ agingFilter: ">=10" });
    } else if (this.getChartSegmentData(e) == " > 20 Days") {
      this.viewIncident({ agingFilter: ">=20" });
    } else if (this.getChartSegmentData(e) == " > 30 Days") {
      this.viewIncident({ agingFilter: ">=30" });
    } else if (this.getChartSegmentData(e) == " > 60 Days") {
      this.viewIncident({ agingFilter: ">60" });
    }
  }

  orgAdminIncidentSegmentClicked(e: any) {
    for (let k in this.orgAdminTilesData) {
      if (this.orgAdminTilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.orgAdminTilesData[k].detailDataParams);
      } else {
        continue;
      }
    }
  }

  orgAdmin1IncidentSegmentClicked(e: any) {
    for (let k in this.orgAdmin1TilesData) {
      if (this.orgAdmin1TilesData[k].name == this.getChartSegmentData(e)) {
        this.viewIncident(this.orgAdmin1TilesData[k].detailDataParams);
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

  viewIncident(element: any) {
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



@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Incidents</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
      <div class="col-sm-12">
          <div class="card ">
              <table class="table table-bordered" mat-table [dataSource]="datasource">
                  <ng-container matColumnDef="inc">
                      <th mat-header-cell *matHeaderCellDef>Incident Details</th>
                      <td mat-cell *matCellDef="let element">
                          {{element.incidentNo}}<br />
                          {{element.title}}<br />
                          {{element.priority.name}}<br />
                          {{formatDateTime(element.raisedAt)}}<br />
                      </td>
                  </ng-container>
                  <ng-container matColumnDef="slaAt">
                      <th mat-header-cell *matHeaderCellDef> SLA. Details </th>
                      <td mat-cell *matCellDef="let element">
                          {{formatDateTime(element.slaAt)}}
                      </td>
                  </ng-container>
                  <ng-container matColumnDef="assignedUser">
                      <th mat-header-cell *matHeaderCellDef> Assigned User </th>
                      <td [attr.data-label]="element.status" mat-cell *matCellDef="let element">
                          {{element.assignedUser?.firstName}}&nbsp;{{element.assignedUser?.lastName}}<br />
                          {{element.assignedUser?.email}}
                      </td>
                  </ng-container>
                  <ng-container matColumnDef="status">
                      <th mat-header-cell *matHeaderCellDef> Status </th>
                      <td [attr.data-label]="element.status" id="id-status-{{element.id}}" mat-cell
                          *matCellDef="let element">
                          {{element.status}}
                      </td>
                  </ng-container>
                  <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                  <tr mat-row *matRowDef="let row; let even = even; columns: displayedColumns;"
                      [ngClass]="{gray: even}"></tr>
                  <tr class="mat-row" *matNoDataRow>
                      <td class="mat-cell" align="center" colspan="9999">
                          No data found!
                      </td>
                  </tr>
              </table>
              <mat-paginator showFirstLastButtons [pageSizeOptions]="[ 10, 25, 100]" [pageSize]="10">
              </mat-paginator>
          </div>
      </div>
  </div>
</div>`
})
export class ViewUserDashboardDetails implements OnInit {
  params: any;
  displayedColumns = ['inc', 'slaAt', 'assignedUser', 'status',];
  private datasource: IncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: IncidentService,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
  ) {
  }
  ngOnInit(): void {
    this.datasource = new IncidentDataSource(this.service);
    this.datasource.loadData(0, 5, this.params);
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
        tap(() => this.loadData(this.params))
      )
      .subscribe();
  }

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }
  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}


function ViewOrgAdminVendorDetail(ViewOrgAdminVendorDetail: any, arg1: ModalOptions<Object> & { initialState: { params: any; }; }): BsModalRef<any> {
  throw new Error('Function not implemented.');
}

