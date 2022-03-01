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
import { EscalationReportComponent } from './escalationreport.component';
import { ReopenReportComponent } from './reopenreport.component';
import { AgingReportComponent } from './agingreport.component';
import { NgSelectModule } from '@ng-select/ng-select';

@NgModule({
  declarations: [
    ReportComponent,
    AdminReportComponent,
    IncidentReportComponent,
    EscalationReportComponent,
    ReopenReportComponent,
    AgingReportComponent,
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
    NgSelectModule,
  ]
})
export class ReportModule { }
