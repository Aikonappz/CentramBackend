import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DepartmentComponent } from './department.component';
import { EditDepartmentComponent } from './editdepartment.component';
import { EditLocationComponent } from './editlocation.component';
import { EditPriorityComponent } from './editpriority.component';
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
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterRoutingModule { }
