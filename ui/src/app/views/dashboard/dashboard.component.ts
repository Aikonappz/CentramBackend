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

@Component({
  templateUrl: 'dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  roles: string[];
  loggedInUser: LoggedInUser;
  adminDashboard: AdminDashboardVO = new AdminDashboardVO();
  orgAdminDashboardVO: OrgAdminDashboardVO = new OrgAdminDashboardVO();
  userDashboardVO: UserDashboardVO = new UserDashboardVO();
  modalRef: BsModalRef;
  firstTabLoaded: boolean = false;

  public adminDoughnutChartLabels: Label[] = [];
  public adminDoughnutChartData: MultiDataSet = [];
  public adminDoughnutChartType: ChartType = 'doughnut';
  public adminChartColors: any[] = [{
    backgroundColor: ["#FF7360", "#6FC8CE", "#adff2f", "#6062ce", "#fd72e6"]
  }];
  public adminDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public orgAdminDoughnutChartLabels: Label[] = [];
  public orgAdminDoughnutChartData: MultiDataSet = [];
  public orgAdminDoughnutChartType: ChartType = 'doughnut';
  public orgAdminChartColors: any[] = [{
    backgroundColor: ["#FF7360", "#6FC8CE", "#adff2f", "#6062ce", "#fd72e6"]
  }];
  public orgAdminDoughnutChartOptions: any = {
    responsive: true,
    boxWidth: 1,
  };

  public userDoughnutChartLabels: Label[] = [];
  public userDoughnutChartData: MultiDataSet = [];
  public userDoughnutChartType: ChartType = 'doughnut';
  public userChartColors: any[] = [{
    backgroundColor: ["#FF7360", "#6FC8CE", "#adff2f", "#6062ce", "#fd72e6"]
  }];
  public userDoughnutChartOptions: any = {
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
    this.adminDashboard = new AdminDashboardVO();
    this.orgAdminDashboardVO = new OrgAdminDashboardVO();
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
          dataPoints.push(this.adminDashboard.activeCompanies);
          dataPoints.push(this.adminDashboard.inactiveCompanies);
          dataPoints.push(this.adminDashboard.allLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.assetLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.incidentLicenceTypeCompanies);
          this.adminDoughnutChartLabels = [
            "Active Organization",
            "Inactive Organization",
            "All License Type Organization",
            "Asset License Type Organization",
            "Incident License Type Organization",
          ];
          this.adminDoughnutChartData = [
            dataPoints
          ];
          this.adminDoughnutChartType = 'doughnut';
        });
      this.firstTabLoaded = true;
    }
    if (this.roles.includes('ORG_ADMIN')) {
      if (this.firstTabLoaded === false) {
        this.firstTabLoaded = true;
        this.service
          .orgAdminDashboard()
          .subscribe((data: OrgAdminDashboardVO) => {
            this.orgAdminDashboardVO = data;
            let dataPoints = [];
            dataPoints.push(this.orgAdminDashboardVO.activeEmployees);
            dataPoints.push(this.orgAdminDashboardVO.inHouseAgents);
            dataPoints.push(this.orgAdminDashboardVO.outSourcedAgents);
            this.orgAdminDoughnutChartLabels = [
              "Active Employees",
              "In House Agents",
              "Out Sourced Agents",
            ];
            this.orgAdminDoughnutChartData = [
              dataPoints
            ];
            this.orgAdminDoughnutChartType = 'doughnut';
          });
      }
    }
    if (this.hasUserRole()) {
      if (this.firstTabLoaded === false) {
        this.firstTabLoaded = true;
        this.service
          .userDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
          .subscribe((data: UserDashboardVO) => {
            this.userDashboardVO = data;
            let dataPoints = [];
            dataPoints.push(this.userDashboardVO.openIncidents);
            dataPoints.push(this.userDashboardVO.assignedIncidents);
            dataPoints.push(this.userDashboardVO.closedIncidents);
            this.userDoughnutChartLabels = [
              "Open Incidents",
              "Assign Incidents",
              "Closed Incidents",
            ];
            this.userDoughnutChartData = [
              dataPoints
            ];
            this.userDoughnutChartType = 'doughnut';
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
          dataPoints.push(this.adminDashboard.activeCompanies);
          dataPoints.push(this.adminDashboard.inactiveCompanies);
          dataPoints.push(this.adminDashboard.allLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.assetLicenceTypeCompanies);
          dataPoints.push(this.adminDashboard.incidentLicenceTypeCompanies);
          this.adminDoughnutChartLabels = [
            "Active Organization",
            "Inactive Organization",
            "All License Type Organization",
            "Asset License Type Organization",
            "Incident License Type Organization",
          ];
          this.adminDoughnutChartData = [
            dataPoints
          ];
          this.adminDoughnutChartType = 'doughnut';
        });
    }
  }

  loadOrgAdminData() {
    if (this.orgAdminDashboardVO.activeEmployees < 0) {
      this.service
        .orgAdminDashboard()
        .subscribe((data: OrgAdminDashboardVO) => {
          this.orgAdminDashboardVO = data;
          let dataPoints = [];
          dataPoints.push(this.orgAdminDashboardVO.activeEmployees);
          dataPoints.push(this.orgAdminDashboardVO.inHouseAgents);
          dataPoints.push(this.orgAdminDashboardVO.outSourcedAgents);
          this.orgAdminDoughnutChartLabels = [
            "Active Employees",
            "In House Agents",
            "Out Sourced Agents",
          ];
          this.orgAdminDoughnutChartData = [
            dataPoints
          ];
          this.orgAdminDoughnutChartType = 'doughnut';
        });
    }
  }

  loadUserData() {
    if (this.userDashboardVO.openIncidents < 0) {
      this.service
        .userDashboard({ currentDate: moment().tz(this.loggedInUser.timeZone).format("YYYY-MM-DD") })
        .subscribe((data: UserDashboardVO) => {
          this.userDashboardVO = data;
          let dataPoints = [];
          dataPoints.push(this.userDashboardVO.openIncidents);
          dataPoints.push(this.userDashboardVO.assignedIncidents);
          dataPoints.push(this.userDashboardVO.closedIncidents);
          this.userDoughnutChartLabels = [
            "Open Incidents",
            "Assign Incidents",
            "Closed Incidents",
          ];
          this.userDoughnutChartData = [
            dataPoints
          ];
          this.userDoughnutChartType = 'doughnut';
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
    if (e.active.length > 0) {
      const chart = e.active[0]._chart;
      const activePoints = chart.getElementAtEvent(e.event);
      if (activePoints.length > 0) {
        // get the internal index of slice in pie chart
        const clickedElementIndex = activePoints[0]._index;
        const label = chart.data.labels[clickedElementIndex];
        // get value by index
        const value = chart.data.datasets[0].data[clickedElementIndex];
        console.log(clickedElementIndex, label, value);

      }
    }
  }

  chartClicked(e: any) {
    if (e.active.length > 0) {
      const chart = e.active[0]._chart;
      const activePoints = chart.getElementAtEvent(e.event);
      if (activePoints.length > 0) {
        // get the internal index of slice in pie chart
        const clickedElementIndex = activePoints[0]._index;
        const label = chart.data.labels[clickedElementIndex];
        // get value by index
        const value = chart.data.datasets[0].data[clickedElementIndex];
        console.log(clickedElementIndex, label, value);

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
                  <ng-container matColumnDef="subCategories">
                      <th class="w-20" mat-header-cell *matHeaderCellDef> Sub Category Access </th>
                      <td mat-cell *matCellDef="let element">
                        {{element.subCategories.join(',')}}
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
export class ViewOrgAdminDashboardDetails implements OnInit {
  params: any;
  displayedColumns = ['name', 'email', 'contact', 'categories', 'subCategories'];
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
