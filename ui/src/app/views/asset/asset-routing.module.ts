import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddAssetComponent } from './add-asset.component';
import { AssetOrderComponent } from './asset-order.component';
import { OrderActionComponent } from './order-action.component';
import { RequestedAssetComponent } from './requested-asset.component';
import { AssetInventoryComponent } from './asset-inventory.component';
import { OrderedAssetComponent } from './ordered-asset.component';
import { RequestAssetComponent } from './request-asset.component';
import { AssetRequestActionComponent } from './action-asset.component';
import { IncommingRequestedAssetComponent } from './incomming-requested-asset.component';
import { MyAssetComponent } from './my-asset.component';
import { OrderedAssetActionComponent } from './in-ordered-asset.component';

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
        path: 'order/incommig',
        component: OrderedAssetActionComponent,
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
        path: 'inventory',
        component: AssetInventoryComponent,
        pathMatch: 'full',
        data: {
          title: 'Assets Inventory'
        },
      },
      {
        path: 'inventory/add',
        component: AddAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Add Asset'
        },
      },
      {
        path: 'inventory/edit/:id',
        component: AddAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Asset'
        }
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
        path: 'assigned',
        component: MyAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'My Assets'
        },
      },
      {
        path: ':referer/add/:mode',
        component: RequestAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Create Incident'
        }
      },
      {
        path: ':referer/edit/:id',
        component: RequestAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Incident'
        }
      },
      {
        path: 'request/action/:requestId',
        component: AssetRequestActionComponent,
        pathMatch: 'full',
        data: {
          title: 'Take Action On Asset Request'
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
