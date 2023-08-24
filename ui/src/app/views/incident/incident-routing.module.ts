import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AgentIncidentComponent } from './agentincident.component';
import { AssignedIncidentComponent } from './assignedincident.component';
import { EditIncidentComponent } from './editincident.component';
import { UserIncidentComponent } from './userincident.component';
import { IncidentDashboardComponent } from './dashboard/incident-dashboard.component';
import { IncidentReportComponent } from './report/incidentreport.component';
import { EscalationReportComponent } from './report/escalationreport.component';
import { ReopenReportComponent } from './report/reopenreport.component';
import { AgingReportComponent } from './report/agingreport.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Incident'
    },
    children: [
      {
        path: '',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: IncidentDashboardComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Dashboard'
        },
      }, 
      {
        path: 'user/all',
        component: UserIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'My Incidents'
        },
      },
      {
        path: 'agent/all',
        component: AgentIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'My Group Incidents'
        }
      },
      {
        path: 'agent/mine',
        component: AssignedIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Assigned Incidents'
        }
      },
      {
        path: ':referer/add/:mode',
        component: EditIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Create Incident'
        }
      },
      {
        path: ':referer/edit/:id',
        component: EditIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Incident'
        }
      },
      {
        path: 'report/incident-report',
        component: IncidentReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/escalation-report',
        component: EscalationReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/reopen-report',
        component: ReopenReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/aging-report',
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
export class IncidentRoutingModule { }
