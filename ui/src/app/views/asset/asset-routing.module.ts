import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddAssetComponent } from './addasset.component';
import { AddOrderComponent } from './addorder.component';
import { ApproveOrderComponent } from './approveorder.component';
import { AssetComponent } from './asset.component';
import { OrderAssetComponent } from './orderasset.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Asset'
    },
    children: [
      {
        path: '',
        redirectTo: 'order'
      },
      {
        path: 'order',
        component: OrderAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Ordered Assets'
        },
      },
      {
        path: 'order/add',
        component: AddOrderComponent,
        pathMatch: 'full',
        data: {
          title: 'Order Assets'
        },
      },
      {
        path: 'order/:approver/approve/:id',
        component: ApproveOrderComponent,
        pathMatch: 'full',
        data: {
          title: 'Approve Assets Order'
        },
      },

      {
        path: 'manage',
        component: AssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Order Assets'
        },
      },
      {
        path: 'manage/add',
        component: AddAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Assets'
        },
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AssetRoutingModule { }
