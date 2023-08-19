import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditUserComponent } from './edituser.component';

import { UserComponent } from './user.component';
import { UserSettingsComponent } from './usersettings.component';

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
        pathMatch: 'full',
        data: {
          title: 'Add User'
        }
      },
      {
        path: 'edit/:id',
        component: EditUserComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit User'
        }
      },
      {
        path: 'settings',
        component: UserSettingsComponent,
        pathMatch: 'full',
        data: {
          title: 'User Settings'
        },
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
