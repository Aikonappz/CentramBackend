import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { OrganisationComponent } from './organisation/organisation.component';
import { EditOrganisationComponent } from './organisation/edit-organisation.component';
import { OrgSettingsComponent } from './organisation/orgsettings.component';
import { UserComponent } from './user/user.component';
import { EditUserComponent } from './user/edituser.component';
import { UserSettingsComponent } from './user/usersettings.component';
import { PermissionComponent } from './permission/permission.component';
import { NotificationComponent } from './notification/notification.component';
import { AdminReportComponent } from './report/adminreport.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Super Admin'
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
        path: 'organization',
        component: OrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Organization'
        },
      },
      {
        path: 'organization/add',
        component: EditOrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Organization'
        }
      },
      {
        path: 'organization/edit/:id',
        component: EditOrganisationComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Organization'
        }
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
        path: 'permission',
        component: PermissionComponent,
        pathMatch: 'full',
        data: {
          title: 'Permission'
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
