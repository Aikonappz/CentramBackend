import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UATActivityComponent } from './uat-activity.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'UAT'
    },
    children: [
      {
        path: '',
        redirectTo: 'activities'
      },      
      {
        path: 'activities',
        component: UATActivityComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Activities'
        },
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UATRoutingModule { }
