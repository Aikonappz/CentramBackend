import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AgentIncidentComponent } from './agentincident.component';
import { AssignedIncidentComponent } from './assignedincident.component';
import { EditIncidentComponent } from './editincident.component';
import { UserIncidentComponent } from './userincident.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Incident'
    },
    children: [
      {
        path: '',
        redirectTo: 'user/all'
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
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IncidentRoutingModule { }
