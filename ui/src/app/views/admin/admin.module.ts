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
  ]
})
export class AdminModule { }
