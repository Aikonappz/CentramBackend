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
import { HolidayCalendarComponent } from './holidaycalendar.component';
import { EditHolidayCalendarComponent } from './editholidaycalendar.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { DistributionListComponent } from './distributionlist.component';
import { EditDlComponent } from './editdl.component';
import { EditVendorComponent } from './editvendor.component';
import { VendorComponent } from './vendor.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { ProjectComponent } from './project.component';
import { EditProjectComponent } from './editproject.component';
import { AccountComponent, } from './account.component';
import { EditAccountComponent } from './editaccount.component';

@NgModule({
  declarations: [
    HolidayCalendarComponent,
    EditHolidayCalendarComponent,
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
    AccountComponent,
    EditAccountComponent,
     ProjectComponent,
        EditProjectComponent,
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
    NgSelectModule,
  ]
})
export class MasterModule { }
