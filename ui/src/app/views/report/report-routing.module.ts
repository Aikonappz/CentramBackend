import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminReportComponent } from './adminreport.component';
import { AgingReportComponent } from './agingreport.component';
import { VendorReportComponent } from './vendor.report.component';
import { EscalationReportComponent } from './escalationreport.component';
import { IncidentReportComponent } from './incidentreport.component';
import { ReopenReportComponent } from './reopenreport.component';
import { ReportComponent } from './report.component';
import { OrderReportComponent } from './order.report.component';
import { AssetTicketReportComponent } from './asset.ticket.report.component';

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
      {
        path: 'vendor-report/:licenceType',
        component: VendorReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Vendor Report'
        },
      },
      {
        path: 'order-report',
        component: OrderReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Order Report'
        },
      },
      {
        path: 'asset-tickets-report',
        component: AssetTicketReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Tickets Report'
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
