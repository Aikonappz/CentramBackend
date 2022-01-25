import { Component, OnInit, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { DashboardService } from '../../service/DashboardService';
import { AdminDashboardVO } from '../../model/AdminDashboardVO';
import { FormBuilder } from '@angular/forms';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { OrganisationService } from '../../service/OrganisationService';
import { OrganisationDataSource } from '../../service/datasource/OrganisationDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { Label, MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';
import { OrgAdminDashboardVO } from '../../model/OrgAdminDashboardVO';
import { UserDataSource } from '../../service/datasource/UserDataSource';
import { UserService } from '../../service/UserService';
import { UserDashboardVO } from '../../model/UserDashboardVO';
import { IncidentDataSource } from '../../service/datasource/IncidentDataSource';
import { IncidentService } from '../../service/IncidentService';
import { AgentDashboardVO } from '../../model/AgentDashboardVO';
import { CategoryAdminDashboardVO } from '../../model/CategoryAdminDashboardVO';

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

  public adminDoughnutChartLabels: Label[] = [];
  public adminDoughnutChartData: MultiDataSet = [];
  public adminDoughnutChartType: ChartType = 'pie';
  public adminChartColors: any[] = [{ backgroundColor: ["#63C2DE", "#FAC008", "#3DA8D8"] }];
  public adminDoughnutChartOptions: any = { responsive: true, boxWidth: 1, };

  public admin1DoughnutChartLabels: Label[] = [];
  public admin1DoughnutChartData: MultiDataSet = [];
  public admin1DoughnutChartType: ChartType = 'pie';
  //public admin1ChartColors: any[] = [{ backgroundColor: ["#adff2f", "#EE6A6C", "#5FBD74", "#3B5998",] }];
  public admin1ChartColors: any[] = [{ backgroundColor: ["#EE6A6C", "#5FBD74", "#3B5998",] }];
  public admin1DoughnutChartOptions: any = { responsive: true, boxWidth: 1, };

  public orgAdminDoughnutChartLabels: Label[] = [];
  public orgAdminDoughnutChartData: MultiDataSet = [];
  public orgAdminDoughnutChartType: ChartType = 'pie';
  public orgAdminChartColors: any[] = [{
    backgroundColor: ["#FF7360", "#6FC8CE", "#adff2f", "#6062ce", "#fd72e6"]
  }];
  public orgAdminDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public orgAdmin1DoughnutChartLabels: Label[] = [];
  public orgAdmin1DoughnutChartData: MultiDataSet = [];
  public orgAdmin1DoughnutChartType: ChartType = 'pie';
  public orgAdmin1ChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#5FBD74", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
  }];
  public orgAdmin1DoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public userDoughnutChartLabels: Label[] = [];
  public userDoughnutChartData: MultiDataSet = [];
  public userDoughnutChartType: ChartType = 'pie';
  public userChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#5FBD74", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
  }];
  public userDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public agentDoughnutChartLabels: Label[] = [];
  public agentDoughnutChartData: MultiDataSet = [];
  public agentDoughnutChartType: ChartType = 'pie';
  public agentChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#5FBD74", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
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

  public caDoughnutChartLabels: Label[] = [];
  public caDoughnutChartData: MultiDataSet = [];
  public caDoughnutChartType: ChartType = 'pie';
  public caChartColors: any[] = [{
    backgroundColor: ["#63CA96", "#E9518B", "#5FBD74", "#7048C1", "#F8CB00", "#3B5998", "#EE6A6C", "#42A3B8", "#ffc107", "#f86c6b", "#6f42c1"]
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
          .orgAdminDashboard()
          .subscribe((data: OrgAdminDashboardVO) => {
            this.orgAdminDashboardVO = new OrgAdminDashboardVO(data);
            let dataPoints = [];
            dataPoints.push(this.orgAdminDashboardVO.activeEmployees);
            dataPoints.push(this.orgAdminDashboardVO.inHouseVendors);
            dataPoints.push(this.orgAdminDashboardVO.outSourcedVendors);
            this.orgAdminDoughnutChartLabels = [
              "Active Employees",
              "In House Vendors",
              "Out Sourced Vendors",
            ];
            this.orgAdminDoughnutChartData = [
              dataPoints
            ];
            this.orgAdminDoughnutChartType = 'pie';
            dataPoints = [];
            let lebel = [];
            for (let i in this.orgAdminDashboardVO.incidents) {
              dataPoints.push(this.orgAdminDashboardVO.incidents[i].count);
              this.orgAdmin1DoughnutChartLabels.push(this.orgAdminDashboardVO.incidents[i].statusName);
            }
            this.orgAdmin1DoughnutChartData = [
              dataPoints
            ];
            this.orgAdmin1DoughnutChartType = 'pie';
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
            for (let i in this.userDashboardVO.incidents) {
              dataPoints.push(this.userDashboardVO.incidents[i].count);
              this.userDoughnutChartLabels.push(this.userDashboardVO.incidents[i].statusName);
            }
            this.userDoughnutChartData = [
              dataPoints
            ];
            this.userDoughnutChartType = 'pie';
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
            for (let i in this.agentDashboardVO.statusIncidents) {
              dataPoints.push(this.agentDashboardVO.statusIncidents[i].count);
              this.agentDoughnutChartLabels.push(this.agentDashboardVO.statusIncidents[i].statusName);
            }
            this.agentDoughnutChartData = [
              dataPoints
            ];
            this.agentDoughnutChartType = 'pie';
            dataPoints = [];
            for (let i in this.agentDashboardVO.priorityIncidents) {
              dataPoints.push(this.agentDashboardVO.priorityIncidents[i].count);
              this.agent1DoughnutChartLabels.push(this.agentDashboardVO.priorityIncidents[i].priority);
            }
            this.agent1DoughnutChartData = [
              dataPoints
            ];
            this.agent1DoughnutChartType = 'pie';
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
            for (let i in this.categoryAdminDashboardVO.statusIncidents) {
              dataPoints.push(this.categoryAdminDashboardVO.statusIncidents[i].count);
              this.caDoughnutChartLabels.push(this.categoryAdminDashboardVO.statusIncidents[i].statusName);
            }
            this.caDoughnutChartData = [
              dataPoints
            ];
            this.caDoughnutChartType = 'pie';
            dataPoints = [];
            for (let i in this.categoryAdminDashboardVO.priorityIncidents) {
              dataPoints.push(this.categoryAdminDashboardVO.priorityIncidents[i].count);
              this.ca1DoughnutChartLabels.push(this.categoryAdminDashboardVO.priorityIncidents[i].priority);
            }
            this.ca1DoughnutChartData = [
              dataPoints
            ];
            this.ca1DoughnutChartType = 'pie';
            dataPoints = [];
            dataPoints.push(this.categoryAdminDashboardVO.aging5);
            dataPoints.push(this.categoryAdminDashboardVO.aging10);
            dataPoints.push(this.categoryAdminDashboardVO.aging20);
            dataPoints.push(this.categoryAdminDashboardVO.aging30);
            dataPoints.push(this.categoryAdminDashboardVO.aging60);
            this.ca2DoughnutChartData = [
              dataPoints
            ];
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
          this.orgAdminDoughnutChartLabels = [
            "Active Employees",
            "In House Vendors",
            "Out Sourced Vendors",
          ];
          this.orgAdminDoughnutChartData = [
            dataPoints
          ];
          this.orgAdminDoughnutChartType = 'pie';
          dataPoints = [];
          let lebel = [];
          for (let i in this.orgAdminDashboardVO.incidents) {
            dataPoints.push(this.orgAdminDashboardVO.incidents[i].count);
            this.orgAdmin1DoughnutChartLabels.push(this.orgAdminDashboardVO.incidents[i].statusName);
          }
          this.orgAdmin1DoughnutChartData = [
            dataPoints
          ];
          this.orgAdmin1DoughnutChartType = 'pie';
        });
    }
  }

  loadUserData() {
    if (this.userDashboardVO.incidents.length == 0) {
      this.service
        .userDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: UserDashboardVO) => {
          this.userDashboardVO = new UserDashboardVO(data);
          let dataPoints = [];
          for (let i in this.userDashboardVO.incidents) {
            dataPoints.push(this.userDashboardVO.incidents[i].count);
            this.userDoughnutChartLabels.push(this.userDashboardVO.incidents[i].statusName);
          }
          this.userDoughnutChartData = [
            dataPoints
          ];
          this.userDoughnutChartType = 'pie';
        });
    }
  }

  loadAgentData() {
    if (this.agentDashboardVO.statusIncidents.length == 0) {
      this.service
        .agentDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: AgentDashboardVO) => {
          this.agentDashboardVO = new AgentDashboardVO(data);
          let dataPoints = [];
          for (let i in this.agentDashboardVO.statusIncidents) {
            dataPoints.push(this.agentDashboardVO.statusIncidents[i].count);
            this.agentDoughnutChartLabels.push(this.agentDashboardVO.statusIncidents[i].statusName);
          }
          this.agentDoughnutChartData = [
            dataPoints
          ];
          this.agentDoughnutChartType = 'pie';
          dataPoints = [];
          for (let i in this.agentDashboardVO.priorityIncidents) {
            dataPoints.push(this.agentDashboardVO.priorityIncidents[i].count);
            this.agent1DoughnutChartLabels.push(this.agentDashboardVO.priorityIncidents[i].priority);
          }
          this.agent1DoughnutChartData = [
            dataPoints
          ];
          this.agent1DoughnutChartType = 'pie';
        });
    }
  }

  loadCategoryAdminData() {
    if (this.categoryAdminDashboardVO.statusIncidents.length == 0) {
      this.service
        .categoryAdminDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: CategoryAdminDashboardVO) => {
          this.categoryAdminDashboardVO = new CategoryAdminDashboardVO(data);
          let dataPoints = [];
          for (let i in this.categoryAdminDashboardVO.statusIncidents) {
            dataPoints.push(this.categoryAdminDashboardVO.statusIncidents[i].count);
            this.caDoughnutChartLabels.push(this.categoryAdminDashboardVO.statusIncidents[i].statusName);
          }
          this.caDoughnutChartData = [
            dataPoints
          ];
          this.caDoughnutChartType = 'pie';
          dataPoints = [];
          for (let i in this.categoryAdminDashboardVO.priorityIncidents) {
            dataPoints.push(this.categoryAdminDashboardVO.priorityIncidents[i].count);
            this.ca1DoughnutChartLabels.push(this.categoryAdminDashboardVO.priorityIncidents[i].priority);
          }
          this.ca1DoughnutChartData = [
            dataPoints
          ];
          this.ca1DoughnutChartType = 'pie';
          dataPoints = [];
          dataPoints.push(this.categoryAdminDashboardVO.aging5);
          dataPoints.push(this.categoryAdminDashboardVO.aging10);
          dataPoints.push(this.categoryAdminDashboardVO.aging20);
          dataPoints.push(this.categoryAdminDashboardVO.aging30);
          dataPoints.push(this.categoryAdminDashboardVO.aging60);
          this.ca2DoughnutChartData = [
            dataPoints
          ];
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
    // if (this.getChartSegmentData(e) === "Total") {
    //   this.viewSiteAdmin({ "status": "ALL" });
    // } else if (this.getChartSegmentData(e) === "Active") {
    //   this.viewSiteAdmin({ "status": "ACTIVE" })
    // } else if (this.getChartSegmentData(e) === "Inactive") {
    //   this.viewSiteAdmin({ "status": "INACTIVE" })
    // } else {
    //   this.viewSiteAdmin({ "status": "ALL" });
    // }
  }

  appAdminChart2SegmentClicked(e: any) {
    // if (this.getChartSegmentData(e) === "Total") {
    //   this.viewSiteAdmin({ "status": "ALL" });
    // } else if (this.getChartSegmentData(e) === "All License Type") {
    //   this.viewSiteAdmin({ "status": "ALL", "licenseType": "ALL" })
    // } else if (this.getChartSegmentData(e) === "Asset License Type") {
    //   this.viewSiteAdmin({ "status": "ALL", "licenseType": "ASSET" })
    // } else if (this.getChartSegmentData(e) === "Incident License Type") {
    //   this.viewSiteAdmin({ "status": "ALL", "licenseType": "INCIDENT" })
    // } else {
    //   this.viewSiteAdmin({ "status": "ALL" });
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
        //console.log(clickedElementIndex, label, value);
        return label;
      }
    }
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

  viewOrgAdmin(element: any) {
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
    this.modalRef = this.modalService.show(ViewOrgAdminDashboardDetails,
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
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Organization</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
      <div class="col-sm-12">
          <div class="card ">
              <table mat-table [dataSource]="datasource">
                  <ng-container matColumnDef="name">
                      <th mat-header-cell *matHeaderCellDef> Name </th>
                      <td mat-cell *matCellDef="let element">
                          {{element.name}}
                      </td>
                  </ng-container>
                  <ng-container matColumnDef="addressDtl">
                      <th mat-header-cell *matHeaderCellDef> Address </th>
                      <td mat-cell *matCellDef="let element">
                          {{element.add1}}<br />
                          {{element.add2}}<br />
                          {{element.city}}<br />
                          {{element.pincode}}
                      </td>
                  </ng-container>
                  <ng-container matColumnDef="licence">
                      <th mat-header-cell *matHeaderCellDef> Licence Dtl. </th>
                      <td mat-cell *matCellDef="let element">
                          {{element.licenseType}}<br />
                          {{formatDate(element.licenseStart)}}<br />
                          {{formatDate(element.licenseEnd)}}
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
              <mat-paginator [pageSizeOptions]="[ 10, 25, 100]" [pageSize]="10"></mat-paginator>
          </div>
      </div>
  </div>
</div>`
})
export class ViewAppAdminDashboardDetails implements OnInit {
  params: any;
  displayedColumns = ['name', 'addressDtl', 'licence', 'status'];
  private datasource: OrganisationDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: OrganisationService,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
  ) {
  }
  ngOnInit(): void {
    this.datasource = new OrganisationDataSource(this.service);
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
}

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Employees/Agent</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
      <div class="card ">
        <div class="row">
          <div class=" col col-sm-5">
            <label class="form-col-form-label" for="moduleId">Vendor</label>
            <!-- <select class="form-control" formControlName="moduleId" ngModel id="moduleId" name="moduleId"> -->
            <select class="form-control" id="moduleId" name="moduleId">
            <option value="" selected>ALL</option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12">
            <table class="table table-bordered" mat-table [dataSource]="datasource">
              <ng-container matColumnDef="name">
                <th class="w-20" mat-header-cell *matHeaderCellDef> Name </th>
                <td mat-cell *matCellDef="let element">
                  {{element.firstName}} {{element.lastName}}
                </td>
              </ng-container>
              <ng-container matColumnDef="email">
                <th class="w-20" mat-header-cell *matHeaderCellDef> Email </th>
                <td mat-cell *matCellDef="let element">
                  {{element.email}}
                </td>
              </ng-container>
              <ng-container matColumnDef="contact">
                <th class="w-20" mat-header-cell *matHeaderCellDef> Contact Dtl. </th>
                <td mat-cell *matCellDef="let element">
                  <b>{{element.contactNo}}</b><br />
                  {{element.secContactNo}}
                </td>
              </ng-container>
              <ng-container matColumnDef="categories">
                <th class="w-20" mat-header-cell *matHeaderCellDef> Category Access </th>
                <td mat-cell *matCellDef="let element">
                  {{element.categories.join(',')}}
                </td>
              </ng-container>
              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; let even = even; columns: displayedColumns;" [ngClass]="{gray: even}">
              </tr>
              <tr class="mat-row" *matNoDataRow>
                <td class="mat-cell" align="center" colspan="9999">
                  No data found!
                </td>
              </tr>
            </table>
            <mat-paginator [pageSizeOptions]="[ 10, 25, 100]" [pageSize]="10"></mat-paginator>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>`
})
export class ViewOrgAdminDashboardDetails implements OnInit {
  params: any;
  displayedColumns = ['name', 'email', 'contact', 'categories',];
  private datasource: UserDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: UserService,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
  ) {
  }
  ngOnInit(): void {
    this.datasource = new UserDataSource(this.service);
    this.datasource.loadUserVOs(0, 5, this.params);
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
    this.datasource.loadUserVOs(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
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
