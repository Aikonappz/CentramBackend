import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditLocationComponent } from './editlocation.component';
import { LocationComponent } from './location.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Location'
    },
    children: [
      {
        path: '',
        component: LocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Location'
        },
      },
      {
        path: 'add',
        component: EditLocationComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Location'
        }
      },
      {
        path: 'edit/:id',
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
export class LocationRoutingModule { }
