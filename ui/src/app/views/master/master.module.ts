import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MasterRoutingModule } from './master-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { DepartmentComponent } from './department.component';
import { EditDepartmentComponent } from './editdepartment.component';
import { LocationComponent } from './location.component';
import { EditLocationComponent } from './editlocation.component';
import { PriorityComponent } from './priority.component';
import { EditPriorityComponent } from './editpriority.component';
import { HolidayCalenderComponent } from './holidaycalender.component';
import { EditHolidayCalenderComponent } from './editholidaycalender.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { DistributionListComponent } from './distributionlist.component';
import { EditDlComponent } from './editdl.component';
import { EditVendorComponent } from './editvendor.component';
import { VendorComponent } from './vendor.component';



@NgModule({
  declarations: [
    HolidayCalenderComponent,
    EditHolidayCalenderComponent,
    PriorityComponent,
    EditPriorityComponent,
    DepartmentComponent,
    EditDepartmentComponent,
    LocationComponent,
    EditLocationComponent,
    DistributionListComponent,
    EditDlComponent,
    VendorComponent,
    EditVendorComponent,
  ],
  imports: [
    CommonModule,
    MasterRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    BsDropdownModule.forRoot(),
  ]
})
export class MasterModule { }
