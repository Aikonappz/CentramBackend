import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UATReportComponent } from './report/uatreport.component';

import { UATActivityComponent } from './activities/uat-activity.component';
import { UATDashboardComponent } from './dashboard/uatdashboard.component';
import { UATProjectComponent } from './master/uatproject.component';
import { EditUATProjectComponent } from './master/edituatproject.component';
import { EditLocationComponent } from './master/editlocation.component';
import { LocationComponent } from './master/location.component';
import { EditDepartmentComponent } from './master/editdepartment.component';
import { DepartmentComponent } from './master/department.component';
import { EditAccountComponent } from './master/editaccount.component';
import { AccountComponent } from './master/account.component';


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
        path: 'master/account',
        component: AccountComponent,
        pathMatch: 'full',
        data: {
          title: 'Account'
        },
      },
      {
        path: 'master/account/add',
        component: EditAccountComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Account'
        }
      },
      {
        path: 'master/account/edit/:id',
        component: EditAccountComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Account'
        }
      },
      {
        path: 'master/department',
        component: DepartmentComponent,
        pathMatch: 'full',
        data: {
          title: 'Department'
        },
      },
      {
        path: 'master/department/add',
        component: EditDepartmentComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Department'
        }
      },
      {
        path: 'master/department/edit/:id',
        component: EditDepartmentComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Department'
        }
      },
      {
        path: 'master/location',
        component: LocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Location'
        },
      },
      {
        path: 'master/location/add',
        component: EditLocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Location'
        }
      },
      {
        path: 'master/location/edit/:id',
        component: EditLocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Location'
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
