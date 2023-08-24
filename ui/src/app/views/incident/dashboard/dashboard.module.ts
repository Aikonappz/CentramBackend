import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChartsModule } from 'ng2-charts';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ViewIncidentDetails } from './modal/ViewIncidentDetails';
import { ViewAppAdminDashboardDetails } from './modal/ViewAppAdminDashboardDetails';
import { ViewOrgAdminDashboardUserDetails } from './modal/ViewOrgAdminDashboardUserDetails';
import { ViewOrgAdminDashboardVendorDetails } from './modal/ViewOrgAdminDashboardVendorDetails';
import { ViewUserDashboardDetails } from './modal/ViewUserDashboardDetails';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    DashboardRoutingModule,
    ChartsModule,
    ButtonsModule.forRoot(),
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    TabsModule,
  ],
  declarations: [
    DashboardComponent,
    ViewUserDashboardDetails,
    ViewIncidentDetails,
    ViewAppAdminDashboardDetails,
    ViewOrgAdminDashboardUserDetails,
    ViewOrgAdminDashboardVendorDetails,
  ]
})
export class DashboardModule { }
