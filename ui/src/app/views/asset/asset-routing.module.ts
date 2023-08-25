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
import { PendingAssetApprovalComponent } from './pending-asset-approval.component';
import { AssetDashboardComponent } from './dashboard/asset-dashboard.component';
import { VendorReportComponent } from './report/vendor.report.component';
import { OrderReportComponent } from './report/order.report.component';
import { AssetTicketReportComponent } from './report/asset.ticket.report.component';
import { AssetAssignmentReportComponent } from './report/asset.asignment.report.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Asset'
    },
    children: [
      {
        path: '',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: AssetDashboardComponent,
        pathMatch: 'full',
        data: {
          title: 'UAT Dashboard'
        },
      }, 
      {
        path: 'order',
        component: AssetOrderComponent,
        pathMatch: 'full',
        data: {
          title: 'Order an Asset'
        },
      },
      {
        path: 'ordered',
        component: OrderedAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Orders Pending My Approval'
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
        path: ':referer/add/:mode',
        component: RequestAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Create Incident'
        }
      },
      {
        path: 'approval/pending',
        component: PendingAssetApprovalComponent,
        pathMatch: 'full',
        data: {
          title: 'Pending Approvals'
        },
      },
      {
        path: 'approval/:requestId',
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
      {
        path: ':referer/edit/:id',
        component: RequestAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit Incident'
        }
      },
      {
        path: 'requested/outgoing',
        component: RequestedAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'Requested Assets'
        },
      },
      {
        path: 'allocated',
        component: MyAssetComponent,
        pathMatch: 'full',
        data: {
          title: 'My Assets'
        },
      },
      {
        path: 'report/vendor-report/:licenceType',
        component: VendorReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Vendor Report'
        },
      },
      {
        path: 'report/order-report',
        component: OrderReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Order Report'
        },
      },
      {
        path: 'report/asset-tickets-report',
        component: AssetTicketReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Tickets Report'
        },
      },
      {
        path: 'report/asset-assignment-report',
        component: AssetAssignmentReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Asset Assignment Report'
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
