import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentRoutingModule } from './incident-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { MasterRoutingModule } from '../master/master-routing.module';
import { EditIncidentComponent } from './editincident.component';
import { CKEditorModule } from 'ckeditor4-angular';
import { UserIncidentComponent } from './userincident.component';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { AssignedIncidentComponent } from './assignedincident.component';
import { AssignIncidentComponent } from './modal/AssignIncidentComponent';
import { AgentIncidentComponent } from './agentincident.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { AddTimeEntryComponent } from './modal/AddTimeEntryComponent';
import { ViewTimeEntryComponent } from './modal/ViewTimeEntryComponent';

import { ViewUserDashboardDetails } from './dashboard/modal/ViewUserDashboardDetails';
import { ViewIncidentDetails } from './dashboard/modal/ViewIncidentDetails';
import { ViewAppAdminDashboardDetails } from './dashboard/modal/ViewAppAdminDashboardDetails';
import { ViewOrgAdminDashboardUserDetails } from './dashboard/modal/ViewOrgAdminDashboardUserDetails';
import { ViewOrgAdminDashboardVendorDetails } from './dashboard/modal/ViewOrgAdminDashboardVendorDetails';
import { IncidentDashboardComponent } from './dashboard/incident-dashboard.component';
import { ChartsModule } from 'ng2-charts';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { IncidentReportComponent } from './report/incidentreport.component';
import { EscalationReportComponent } from './report/escalationreport.component';
import { ReopenReportComponent } from './report/reopenreport.component';
import { AgingReportComponent } from './report/agingreport.component';

@NgModule({
  declarations: [
    UserIncidentComponent,
    EditIncidentComponent,
    AgentIncidentComponent,
    AssignIncidentComponent,
    AssignedIncidentComponent,
    AddTimeEntryComponent,
    ViewTimeEntryComponent,
    IncidentDashboardComponent,
    ViewUserDashboardDetails,
    ViewIncidentDetails,
    ViewAppAdminDashboardDetails,
    ViewOrgAdminDashboardUserDetails,
    ViewOrgAdminDashboardVendorDetails,
    IncidentReportComponent,
    EscalationReportComponent,
    ReopenReportComponent,
    AgingReportComponent,
  ],
  imports: [
    CommonModule,
    IncidentRoutingModule,
    MasterRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    CKEditorModule,
    TooltipModule.forRoot(),
    BsDropdownModule.forRoot(),
    NgSelectModule,
    ChartsModule,
    ButtonsModule.forRoot(),
  ]
})
export class IncidentModule { }
