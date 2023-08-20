import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DepartmentComponent } from './department.component';
import { DistributionListComponent } from './distributionlist.component';
import { EditDepartmentComponent } from './editdepartment.component';
import { EditDlComponent } from './editdl.component';
import { EditHolidayCalendarComponent } from './editholidaycalendar.component';
import { EditLocationComponent } from './editlocation.component';
import { EditPriorityComponent } from './editpriority.component';
import { EditProjectComponent } from './editproject.component';
import { EditVendorComponent } from './editvendor.component';
import { HolidayCalendarComponent } from './holidaycalendar.component';
import { LocationComponent } from './location.component';
import { PriorityComponent } from './priority.component';
import { ProjectComponent } from './project.component';
import { VendorComponent } from './vendor.component';
import { AccountComponent } from './account.component';
import { EditAccountComponent } from './editaccount.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Master'
    },
    children: [
      {
        path: '',
        redirectTo: 'department'
      },
      {
        path: 'account',
        component: AccountComponent,
        pathMatch: 'full',
        data: {
          title: 'Account'
        },
      },
      {
        path: 'account/add',
        component: EditAccountComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Account'
        }
      },
      {
        path: 'account/edit/:id',
        component: EditAccountComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Account'
        }
      },
      {
        path: 'department',
        component: DepartmentComponent,
        pathMatch: 'full',
        data: {
          title: 'Department'
        },
      },
      {
        path: 'department/add',
        component: EditDepartmentComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Department'
        }
      },
      {
        path: 'department/edit/:id',
        component: EditDepartmentComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Department'
        }
      },
      {
        path: 'location',
        component: LocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Location'
        },
      },
      {
        path: 'location/add',
        component: EditLocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Location'
        }
      },
      {
        path: 'location/edit/:id',
        component: EditLocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Location'
        }
      },
      {
        path: 'priority/:type',
        component: PriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Priority'
        },
      },
      {
        path: 'priority/:licenceType/add',
        component: EditPriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Priority'
        }
      },
      {
        path: 'priority/:licenceType/edit/:id',
        component: EditPriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Priority'
        }
      },
      {
        path: 'calendar',
        component: HolidayCalendarComponent,
        pathMatch: 'full',
        data: {
          title: 'Holiday Calendar'
        },
      },
      {
        path: 'calendar/add',
        component: EditHolidayCalendarComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Holiday Calendar'
        }
      },
      {
        path: 'calendar/edit/:id',
        component: EditHolidayCalendarComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Holiday Calendar'
        }
      },
      {
        path: 'dl',
        component: DistributionListComponent,
        pathMatch: 'full',
        data: {
          title: 'Distribution List'
        },
      },
      {
        path: 'dl/add',
        component: EditDlComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Distribution List'
        }
      },
      {
        path: 'dl/edit/:id',
        component: EditDlComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Distribution List'
        }
      },
      {
        path: 'vendor/:type',
        component: VendorComponent,
        pathMatch: 'full',
        data: {
          title: 'Vendor'
        },
      },
      {
        path: 'vendor/:licenceType/add',
        component: EditVendorComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Vendor'
        }
      },
      {
        path: 'vendor/:licenceType/edit/:id',
        component: EditVendorComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Vendor'
        }
      },
      {
        path: 'project',
        component: ProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Project'
        },
      },
      {
        path: 'project/add',
        component: EditProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Project'
        }
      },
      {
        path: 'project/edit/:id',
        component: EditProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Project'
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MasterRoutingModule { }
