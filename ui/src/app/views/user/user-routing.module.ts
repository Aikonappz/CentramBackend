import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditUserComponent } from './edituser.component';

import { UserComponent } from './user.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'User'
    },
    children: [
      {
        path: '',
        component: UserComponent,
        pathMatch: 'full',
        data: {
          title: 'User'
        },
      },
      {
        path: 'add',
        component: EditUserComponent,
        data: {
          title: 'Add User'
        }
      },
      {
        path: 'edit/:id',
        component: EditUserComponent,
        data: {
          title: 'Edit User'
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
