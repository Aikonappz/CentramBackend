import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PermissionComponent } from './permission.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Permission'
    },
    children: [
      {
        path: '',
        component: PermissionComponent,
        pathMatch: 'full',
        data: {
          title: 'Permission'
        },
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PermissionRoutingModule { }
