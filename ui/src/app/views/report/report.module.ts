import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ReportRoutingModule } from './report-routing.module';
import { ReportComponent } from './report.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { AdminReportComponent } from './adminreport.component';
import { IncidentReportComponent } from './incidentreport.component';



@NgModule({
  declarations: [
    ReportComponent,
    AdminReportComponent,
    IncidentReportComponent,
  ],
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    ReportRoutingModule,
    BsDropdownModule.forRoot(),
  ]
})
export class ReportModule { }
