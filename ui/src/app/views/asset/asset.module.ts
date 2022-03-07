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
import { AllocateAsset } from './model/AllocateAsset';
import { NgSelectModule } from '@ng-select/ng-select';
import { ViewAssetOrderDetail } from './model/ViewAssetOrderDetail';
import { ViewAssetDetail } from './model/ViewAssetDetail';


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
    AllocateAsset,
    ViewAssetOrderDetail,
    ViewAssetDetail,
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
