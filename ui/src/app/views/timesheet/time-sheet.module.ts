import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { CKEditorModule } from 'ckeditor4-angular';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { NgSelectModule } from '@ng-select/ng-select';

import { ChartsModule } from 'ng2-charts';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { NgxMaskModule } from 'ngx-mask'
import { TextMaskModule } from 'angular2-text-mask';
import { TimeSheetRoutingModule } from './time-sheet-routing.module';
import { RemarkViewer } from './modal/RemarkViewer';
import { UATActivityComponent } from './activities/uat-activity.component';

import { UATReportComponent } from './report/uatreport.component';
import { TimeSheetDashboardComponent } from './dashboard/uatdashboard.component';
import { EditUATProjectComponent } from './master/edituatproject.component';
import { UATProjectComponent } from './master/uatproject.component';
import { ViewUATDashboardDetails } from './dashboard/modal/ViewUATDashboardDetails';
import { AccountComponent } from './master/account.component';
import { EditAccountComponent } from './master/editaccount.component';
import { DepartmentComponent } from './master/department.component';
import { EditDepartmentComponent } from './master/editdepartment.component';
import { LocationComponent } from './master/location.component';
import { EditLocationComponent } from './master/editlocation.component';
import { AllocateProjectComponent } from './operation/allocate-project.component';
import { DeallocateProjectComponent } from './operation/deallocate-project.component';

import { TimeSheetsComponent } from './manage-timesheet/timesheets.component';
import { TimeSheetApprovalListComponent } from './operation/timesheet-approval-list.component';
import { TimeSheetApprovalComponent } from './operation/timesheet-approval.component';
import { AddTimeSheetComponent } from './manage-timesheet/add-timesheet.component';
import { EditTimeSheetComponent } from './manage-timesheet/edit-timesheet.component';



@NgModule({
  declarations: [
    EditTimeSheetComponent,
    AddTimeSheetComponent,
    TimeSheetApprovalComponent,
    TimeSheetApprovalListComponent,
    TimeSheetsComponent,
    AllocateProjectComponent,
    DeallocateProjectComponent,
    UATActivityComponent,
    RemarkViewer,
    TimeSheetDashboardComponent,
    UATReportComponent,
    UATProjectComponent,
    EditUATProjectComponent,
    ViewUATDashboardDetails,
    AccountComponent,
    EditAccountComponent,
    DepartmentComponent,
    EditDepartmentComponent,
    LocationComponent,
    EditLocationComponent,
    EditTimeSheetComponent,
  ],
  imports: [
    CommonModule,
    TimeSheetRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    CKEditorModule,
    TooltipModule.forRoot(),
    BsDropdownModule.forRoot(),
    NgSelectModule,
    CommonModule,
    FormsModule,
    ChartsModule,
    ButtonsModule.forRoot(),
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    TabsModule,
    TextMaskModule,
    ChartsModule,
    NgxMaskModule.forRoot(),
  ]
})
export class TimesheetModule { }
