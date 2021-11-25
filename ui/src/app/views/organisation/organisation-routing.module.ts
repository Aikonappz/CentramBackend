import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditOrganisationComponent, } from './editorganisation.component';
import { OrganisationComponent } from './organisation.component';
import { OrgSettingsComponent } from './orgsettings.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Organisation'
    },
    children: [
      {
        path: '',
        component: OrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Organisation'
        },
      },
      {
        path: 'add',
        component: EditOrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Organisation'
        }
      },
      {
        path: 'edit/:id',
        component: EditOrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Organisation'
        }
      },
      {
        path: 'settings',
        component: OrgSettingsComponent,
        pathMatch: 'full',
        data: {
          title: 'Organisation Settings'
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrganisationRoutingModule { }
