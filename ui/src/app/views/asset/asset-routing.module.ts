import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddAssetComponent } from './addasset.component';
import { AssetOrderComponent } from './asset-order.component';
import { OrderActionComponent } from './order-action.component';
import { RequestedAssetComponent } from './requested-asset.component';
import { AssetComponent } from './asset.component';
import { OrderedAssetComponent } from './ordered-asset.component';
import { RequestAssetComponent } from './request-asset.component';
import { ActionAssetRequestComponent } from './approveasset.component';
import { IncommingRequestedAssetComponent } from './incomming-requested-asset.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Asset'
    },
    children: [
      {
        path: '',
        redirectTo: 'ordered'
      },
      {
        path: 'ordered',
        component: OrderedAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Ordered Assets'
        },
      },
      {
        path: 'order',
        component: AssetOrderComponent,
        pathMatch: 'full',
        data: {
          title: 'Order Assets'
        },
      },
      {
        path: 'order/:approver/action/:id',
        component: OrderActionComponent,
        pathMatch: 'full',
        data: {
          title: 'Action Assets Order'
        },
      },

      {
        path: 'manage',
        component: AssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Manage Assets'
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
      {
        path: 'requested',
        component: RequestedAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Requested Assets'
        },
      },
      {
        path: 'request',
        component: RequestAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Request Assets'
        },
      },
      {
        path: 'request/action/:requestId',
        component: ActionAssetRequestComponent,
        pathMatch: 'full',
        data: {
          title: 'Request Action'
        },
      },
      {
        path: 'requested/incomming',
        component: IncommingRequestedAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Requested Asset'
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
