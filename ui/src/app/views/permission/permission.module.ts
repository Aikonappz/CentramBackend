import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PermissionRoutingModule } from './permission-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { NgSelectModule } from '@ng-select/ng-select';
import { PermissionComponent } from './permission.component';
import { ViewTaggedModal } from './model/ViewTaggedModule';

@NgModule({
  declarations: [
    PermissionComponent,
    ViewTaggedModal,
  ],
  imports: [
    CommonModule,
    PermissionRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    NgSelectModule,
  ]
})
export class PermissionModule { }
