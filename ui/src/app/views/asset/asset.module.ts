import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AssetRoutingModule } from './asset-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { MasterRoutingModule } from '../master/master-routing.module';
import { CKEditorModule } from 'ckeditor4-angular';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { OrderedAssetComponent } from './ordered-asset.component';
import { AssetOrderComponent } from './asset-order.component';
import { OrderActionComponent } from './order-action.component';
import { AssetInventoryComponent } from './asset-inventory.component';
import { AddAssetComponent } from './add-asset.component';
import { RequestedAssetComponent } from './requested-asset.component';
import { RequestAssetComponent } from './request-asset.component';
import { AssetRequestActionComponent, } from './action-asset.component';
import { IncommingRequestedAssetComponent } from './incomming-requested-asset.component';
import { DeallocateAsset } from './modal/DeallocateAsset';
import { NgSelectModule } from '@ng-select/ng-select';
import { ViewAssetOrderDetail } from './modal/ViewAssetOrderDetail';
import { ViewAssetDetail } from './modal/ViewAssetDetail';
import { UploadAssetComponent } from './modal/UploadAssetComponent';
import { MyAssetComponent } from './my-asset.component';
import { AssignIncidentComponent } from './modal/AssignIncidentComponent';
import { OrderedAssetActionComponent } from './in-ordered-asset.component';
import { PendingAssetApprovalComponent } from './pending-asset-approval.component';
import { ViewAssetRequestDetail } from './modal/ViewAssetRequestDetail';
import { ViewUserDashboardDetails } from './dashboard/modal/ViewUserDashboardDetails';
import { ViewIncidentDetails } from './dashboard/modal/ViewIncidentDetails';
import { ViewAppAdminDashboardDetails } from './dashboard/modal/ViewAppAdminDashboardDetails';
import { ViewOrgAdminDashboardUserDetails } from './dashboard/modal/ViewOrgAdminDashboardUserDetails';
import { ViewOrgAdminDashboardVendorDetails } from './dashboard/modal/ViewOrgAdminDashboardVendorDetails';
import { AssetDashboardComponent } from './dashboard/asset-dashboard.component';
import { VendorReportComponent } from './report/vendor.report.component';
import { OrderReportComponent } from './report/order.report.component';
import { AssetTicketReportComponent } from './report/asset.ticket.report.component';
import { AssetAssignmentReportComponent } from './report/asset.asignment.report.component';


@NgModule({
  declarations: [
    OrderedAssetComponent,
    AssetOrderComponent,
    OrderActionComponent,
    AssetInventoryComponent,
    AddAssetComponent,
    RequestedAssetComponent,
    RequestAssetComponent,
    AssetRequestActionComponent,
    IncommingRequestedAssetComponent,
    ViewAssetOrderDetail,
    ViewAssetDetail,
    UploadAssetComponent,
    MyAssetComponent,
    DeallocateAsset,
    AssignIncidentComponent,
    OrderedAssetActionComponent,
    PendingAssetApprovalComponent,
    ViewAssetRequestDetail,

    AssetDashboardComponent,
    ViewUserDashboardDetails,
    ViewIncidentDetails,
    ViewAppAdminDashboardDetails,
    ViewOrgAdminDashboardUserDetails,
    ViewOrgAdminDashboardVendorDetails,

    VendorReportComponent,
    OrderReportComponent,
    AssetTicketReportComponent,
    AssetAssignmentReportComponent,
  ],
  imports: [
    CommonModule,
    AssetRoutingModule,
    MasterRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    CKEditorModule,
    TooltipModule.forRoot(),
    BsDropdownModule.forRoot(),
    NgSelectModule,
  ]
})
export class AssetModule { }
