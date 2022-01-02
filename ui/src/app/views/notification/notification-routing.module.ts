import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { Notification } from '../../model/Notification';
import { NotificationComponent } from './notification.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Notification'
    },
    children: [
      {
        path: '',
        component: NotificationComponent,
        pathMatch: 'full',
        data: {
          title: 'Notification'
        },
      },
      {
        path: 'view/:id',
        component: NotificationComponent,
        pathMatch: 'full',
        data: {
          title: 'View Notification'
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NotificationRoutingModule { }
