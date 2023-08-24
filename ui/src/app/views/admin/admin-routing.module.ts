import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserComponent } from './user/user.component';
import { EditUserComponent } from './user/edituser.component';
import { UserSettingsComponent } from './user/usersettings.component';
import { NotificationComponent } from './notification/notification.component';
import { AdminReportComponent } from './report/adminreport.component';
import { OrgSettingsComponent } from './organisation/orgsettings.component';
import { EditProjectComponent } from './master/editproject.component';
import { ProjectComponent } from './master/project.component';
import { EditVendorComponent } from './master/editvendor.component';
import { VendorComponent } from './master/vendor.component';
import { EditDlComponent } from './master/editdl.component';
import { DistributionListComponent } from './master/distributionlist.component';
import { EditHolidayCalendarComponent } from './master/editholidaycalendar.component';
import { HolidayCalendarComponent } from './master/holidaycalendar.component';
import { EditPriorityComponent } from './master/editpriority.component';
import { PriorityComponent } from './master/priority.component';
import { EditLocationComponent } from './master/editlocation.component';
import { LocationComponent } from './master/location.component';
import { EditDepartmentComponent } from './master/editdepartment.component';
import { DepartmentComponent } from './master/department.component';
import { EditAccountComponent } from './master/editaccount.component';
import { AccountComponent } from './master/account.component';
import { AssetAssignmentReportComponent } from './report/asset.asignment.report.component';
import { AssetTicketReportComponent } from './report/asset.ticket.report.component';
import { OrderReportComponent } from './report/order.report.component';
import { VendorReportComponent } from './report/vendor.report.component';
import { AgingReportComponent } from './report/agingreport.component';
import { ReopenReportComponent } from './report/reopenreport.component';
import { EscalationReportComponent } from './report/escalationreport.component';
import { IncidentReportComponent } from './report/incidentreport.component';

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
        path: 'master/priority/:type',
        component: PriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Priority'
        },
      },
      {
        path: 'master/priority/:licenceType/add',
        component: EditPriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Priority'
        }
      },
      {
        path: 'master/priority/:licenceType/edit/:id',
        component: EditPriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Priority'
        }
      },
      {
        path: 'master/calendar',
        component: HolidayCalendarComponent,
        pathMatch: 'full',
        data: {
          title: 'Holiday Calendar'
        },
      },
      {
        path: 'master/calendar/add',
        component: EditHolidayCalendarComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Holiday Calendar'
        }
      },
      {
        path: 'master/calendar/edit/:id',
        component: EditHolidayCalendarComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Holiday Calendar'
        }
      },
      {
        path: 'master/dl',
        component: DistributionListComponent,
        pathMatch: 'full',
        data: {
          title: 'Distribution List'
        },
      },
      {
        path: 'master/dl/add',
        component: EditDlComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Distribution List'
        }
      },
      {
        path: 'master/dl/edit/:id',
        component: EditDlComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Distribution List'
        }
      },
      {
        path: 'master/vendor/:type',
        component: VendorComponent,
        pathMatch: 'full',
        data: {
          title: 'Vendor'
        },
      },
      {
        path: 'master/vendor/:licenceType/add',
        component: EditVendorComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Vendor'
        }
      },
      {
        path: 'master/vendor/:licenceType/edit/:id',
        component: EditVendorComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Vendor'
        }
      },
      {
        path: 'master/project',
        component: ProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Project'
        },
      },
      {
        path: 'master/project/add',
        component: EditProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Project'
        }
      },
      {
        path: 'master/project/edit/:id',
        component: EditProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Project'
        }
      },
      {
        path: 'report/incident-report',
        component: IncidentReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/escalation-report',
        component: EscalationReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/reopen-report',
        component: ReopenReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/aging-report',
        component: AgingReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Incident Report'
        },
      },
      {
        path: 'report/vendor-report/:licenceType',
        component: VendorReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Vendor Report'
        },
      },
      {
        path: 'report/order-report',
        component: OrderReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Order Report'
        },
      },
      {
        path: 'report/asset-tickets-report',
        component: AssetTicketReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Tickets Report'
        },
      },
      {
        path: 'report/asset-assignment-report',
        component: AssetAssignmentReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Assignment Report'
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
