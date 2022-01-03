import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DepartmentComponent } from './department.component';
import { DistributionListComponent } from './distributionlist.component';
import { EditDepartmentComponent } from './editdepartment.component';
import { EditDlComponent } from './editdl.component';
import { EditHolidayCalenderComponent } from './editholidaycalender.component';
import { EditLocationComponent } from './editlocation.component';
import { EditPriorityComponent } from './editpriority.component';
import { HolidayCalenderComponent } from './holidaycalender.component';
import { LocationComponent } from './location.component';
import { PriorityComponent } from './priority.component';

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
        path: 'priority',
        component: PriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Priority'
        },
      },
      {
        path: 'priority/add',
        component: EditPriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Priority'
        }
      },
      {
        path: 'priority/edit/:id',
        component: EditPriorityComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Priority'
        }
      },
      {
        path: 'calender',
        component: HolidayCalenderComponent,
        pathMatch: 'full',
        data: {
          title: 'Holiday Calender'
        },
      },
      {
        path: 'calender/add',
        component: EditHolidayCalenderComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Holiday Calender'
        }
      },
      {
        path: 'calender/edit/:id',
        component: EditHolidayCalenderComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Holiday Calender'
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
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterRoutingModule { }
