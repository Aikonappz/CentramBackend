import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AgentIncidentComponent } from './agentincident.component';
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
        redirectTo: 'raised'
      },
      {
        path: 'user',
        component: UserIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident'
        },
      },
      {
        path: 'agent',
        component: AgentIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Incoming Incident'
        }
      },
      {
        path: 'add',
        component: EditIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Raise Incident'
        }
      },
      {
        path: 'edit/:id',
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
