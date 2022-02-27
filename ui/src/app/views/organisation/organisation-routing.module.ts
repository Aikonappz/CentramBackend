import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditOrganisationComponent, } from './edit-organisation.component';
import { OrganisationComponent } from './organisation.component';
import { OrgSettingsComponent } from './orgsettings.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Organization'
    },
    children: [
      {
        path: '',
        component: OrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Organization'
        },
      },
      {
        path: 'add',
        component: EditOrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Organization'
        }
      },
      {
        path: 'edit/:id',
        component: EditOrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Organization'
        }
      },
      {
        path: 'settings',
        component: OrgSettingsComponent,
        pathMatch: 'full',
        data: {
          title: 'Organization Settings'
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
