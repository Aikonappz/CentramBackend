import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChartsModule } from 'ng2-charts';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { DashboardComponent, ViewAppAdminDashboardDetails } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';

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
  ],
  declarations: [
    DashboardComponent,
    ViewAppAdminDashboardDetails
  ]
})
export class DashboardModule { }
