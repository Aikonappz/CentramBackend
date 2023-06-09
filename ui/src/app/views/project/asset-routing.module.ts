import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AllocateProjectComponent } from './allocate-project.component';
import { DeallocateProjectComponent } from './deallocate-project.component';
import { ManageTimeSheetComponent } from './manage-timesheet.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Project'
    },
    children: [
      {
        path: '',
        redirectTo: 'manage-timesheet'
      },
      {
        path: 'manage-timesheet',
        component: ManageTimeSheetComponent,
        pathMatch: 'full',
        data: {
          title: 'Manage Timesheet'
        },
      },
      {
        path: 'allocate',
        component: AllocateProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Allocate Project'
        },
      },
      {
        path: 'deallocate',
        component: DeallocateProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Deallocate Project'
        },
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProjectRoutingModule { }
