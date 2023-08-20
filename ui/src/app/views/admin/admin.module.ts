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
import { UserComponent } from './user/user.component';
import { EditUserComponent } from './user/edituser.component';
import { ViewUserDetail } from './user/modal/ViewUserDetailModal';
import { UploadUserComponent } from './user/modal/UploadUserModal';
import { UserSettingsComponent } from './user/usersettings.component';
import { SupAdminRoutingModule } from './admin-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ViewUserDashboardDetails } from './dashboard/modal/ViewUserDashboardDetails';
import { SafePipe } from '../../service/pipe/SafePipe';
import { NotificationViewComponent } from './notification/modal/NotificationViewComponent';
import { NotificationComponent } from './notification/notification.component';
import { AdminReportComponent } from './report/adminreport.component';
import { OrgSettingsComponent } from './organisation/orgsettings.component';
import { HolidayCalendarComponent } from './master/holidaycalendar.component';
import { EditHolidayCalendarComponent } from './master/editholidaycalendar.component';
import { PriorityComponent } from './master/priority.component';
import { EditPriorityComponent } from './master/editpriority.component';
import { DepartmentComponent } from './master/department.component';
import { EditDepartmentComponent } from './master/editdepartment.component';
import { LocationComponent } from './master/location.component';
import { EditLocationComponent } from './master/editlocation.component';
import { DistributionListComponent } from './master/distributionlist.component';
import { EditDlComponent } from './master/editdl.component';
import { VendorComponent } from './master/vendor.component';
import { EditVendorComponent } from './master/editvendor.component';
import { AccountComponent } from './master/account.component';
import { EditAccountComponent } from './master/editaccount.component';
import { ProjectComponent } from './master/project.component';
import { EditProjectComponent } from './master/editproject.component';



@NgModule({
  declarations: [
    AdminReportComponent,
    NotificationComponent,
    NotificationViewComponent,
    SafePipe,
    DashboardComponent,
    ViewUserDashboardDetails,
    UserComponent,
    EditUserComponent,
    ViewUserDetail,
    UploadUserComponent,
    UserComponent,
    ViewUserDetail,
    EditUserComponent,
    UserSettingsComponent,
    UploadUserComponent,
    OrgSettingsComponent,

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
    SupAdminRoutingModule,
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
    NgxMaskModule.forRoot(),
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    NgSelectModule,
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    BsDropdownModule.forRoot(),
    ReactiveFormsModule,
    TabsModule,
    NgSelectModule,
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    BsDropdownModule.forRoot(),
    NgSelectModule,
  ]
})
export class AdminModule { }
