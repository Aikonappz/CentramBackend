import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DepartmentComponent } from './department.component';
import { EditDepartmentComponent } from './editdepartment.component';
import { EditLocationComponent } from './editlocation.component';
import { LocationComponent } from './location.component';

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
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterRoutingModule { }
