import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminReportComponent } from './adminreport.component';
import { AgingReportComponent } from './agingreport.component';
import { EscalationReportComponent } from './escalationreport.component';
import { IncidentReportComponent } from './incidentreport.component';
import { ReopenReportComponent } from './reopenreport.component';
import { ReportComponent } from './report.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Report'
    },
    children: [
      {
        path: '',
        component: ReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Report'
        },
      },
      {
        path: 'admin-report',
        component: AdminReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Report'
        },
      },
      {
        path: 'incident-report',
        component: IncidentReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'escalation-report',
        component: EscalationReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'reopen-report',
        component: ReopenReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'aging-report',
        component: AgingReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule { }
