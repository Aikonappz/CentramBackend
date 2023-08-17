import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UATReportComponent } from './report/uatreport.component';

import { UATActivityComponent } from './activities/uat-activity.component';
import { UATDashboardComponent } from './dashboard/uatdashboard.component';


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
