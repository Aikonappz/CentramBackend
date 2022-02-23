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
import { OrderAssetComponent } from './orderasset.component';
import { AddOrderComponent } from './addorder.component';
import { ApproveOrderComponent } from './approveorder.component';
import { AssetComponent } from './asset.component';
import { AddAssetComponent } from './addasset.component';
import { RequestedAssetComponent } from './requested-asset.component';
import { RequestAssetComponent } from './request-asset.component';
import { ActionAssetRequestComponent, } from './approveasset.component';


@NgModule({
  declarations: [
    OrderAssetComponent,
    AddOrderComponent,
    ApproveOrderComponent,
    AssetComponent,
    AddAssetComponent,
    RequestedAssetComponent,
    RequestAssetComponent,
    ActionAssetRequestComponent,
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
  ]
})
export class AssetModule { }
