import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UATReportComponent } from './report/uatreport.component';

import { UATActivityComponent } from './activities/uat-activity.component';
import { UATDashboardComponent } from './dashboard/uatdashboard.component';
import { UATProjectComponent } from './master/uatproject.component';
import { EditUATProjectComponent } from './master/edituatproject.component';


const routes: Routes = [
  {
    path: '',
    data: {
      title: 'UAT'
    },
    children: [
      {
        path: '',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: UATDashboardComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Dashboard'
        },
      },
      {
        path: 'master/project',
        component: UATProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Project'
        },
      },
      {
        path: 'master/project/add',
        component: EditUATProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Project'
        }
      },
      {
        path: 'master/project/edit/:id',
        component: EditUATProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Project'
        }
      },
      {
        path: 'activities',
        component: UATActivityComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Activities'
        },
      },
      {
        path: 'report',
        component: UATReportComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Report'
        },
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UATRoutingModule { }
