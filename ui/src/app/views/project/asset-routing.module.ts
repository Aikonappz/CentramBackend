import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AllocateProjectComponent } from './allocate-project.component';
import { DeallocateProjectComponent } from './deallocate-project.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Project'
    },
    children: [
      {
        path: '',
        redirectTo: 'allocate'
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
