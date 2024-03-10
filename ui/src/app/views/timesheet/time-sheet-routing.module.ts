import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UATReportComponent } from './report/uatreport.component';

import { UATActivityComponent } from './activities/uat-activity.component';
import { TimeSheetDashboardComponent } from './dashboard/uatdashboard.component';
import { UATProjectComponent } from './master/uatproject.component';
import { EditUATProjectComponent } from './master/edituatproject.component';
import { EditLocationComponent } from './master/editlocation.component';
import { LocationComponent } from './master/location.component';
import { EditDepartmentComponent } from './master/editdepartment.component';
import { DepartmentComponent } from './master/department.component';
import { EditAccountComponent } from './master/editaccount.component';
import { AccountComponent } from './master/account.component';
import { AllocateProjectComponent } from './operation/allocate-project.component';
import { DeallocateProjectComponent } from './operation/deallocate-project.component';
import { ManageTimeSheetComponent } from './manage-timesheet/manage-timesheet.component';


const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Time Sheet'
    },
    children: [
      {
        path: '',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: TimeSheetDashboardComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Dashboard'
        },
      },
      {
        path: 'operation/allocate',
        component: AllocateProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Allocate Project'
        },
      },
      {
        path: 'operation/deallocate',
        component: DeallocateProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Deallocate Project'
        }
      },
      {
        path: 'manage-timesheet',
        component: ManageTimeSheetComponent,
        pathMatch: 'full',
        data: {
          title: 'Manage TimeSheet'
        }
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TimeSheetRoutingModule { }
