import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserComponent } from './user/user.component';
import { EditUserComponent } from './user/edituser.component';
import { UserSettingsComponent } from './user/usersettings.component';
import { NotificationComponent } from './notification/notification.component';
import { AdminReportComponent } from './report/adminreport.component';
import { OrgSettingsComponent } from './organisation/orgsettings.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Admin'
    },
    children: [
      {
        path: '',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
        pathMatch: 'full',
        data: {
          title: 'Dashboard'
        },
      },
      {
        path: 'organization/settings',
        component: OrgSettingsComponent,
        pathMatch: 'full',
        data: {
          title: 'Organization Settings'
        }
      },
      {
        path: 'user',
        component: UserComponent,
        pathMatch: 'full',
        data: {
          title: 'User'
        },
      },
      {
        path: 'user/add',
        component: EditUserComponent,
        pathMatch: 'full',
        data: {
          title: 'Add User'
        }
      },
      {
        path: 'user/edit/:id',
        component: EditUserComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit User'
        }
      },
      {
        path: 'user/settings',
        component: UserSettingsComponent,
        pathMatch: 'full',
        data: {
          title: 'User Settings'
        },
      },
      {
        path: 'notification',
        component: NotificationComponent,
        pathMatch: 'full',
        data: {
          title: 'Notification'
        },
      },
      {
        path: 'notification/view/:id',
        component: NotificationComponent,
        pathMatch: 'full',
        data: {
          title: 'View Notification'
        }
      },
      {
        path: 'report/admin-report',
        component: AdminReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Report'
        },
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SupAdminRoutingModule { }
