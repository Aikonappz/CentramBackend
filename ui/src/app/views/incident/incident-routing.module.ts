import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IncidentComponent } from './incident.component';
import { RaiseIncidentComponent } from './raiseincident.component';

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
        path: 'raised',
        component: IncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident'
        },
      },
      {
        path: 'raise',
        component: RaiseIncidentComponent,
        pathMatch: 'full',
        data: {
          title: 'Raise Incident'
        }
      },
      {
        path: 'edit/:id',
        component: RaiseIncidentComponent,
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
