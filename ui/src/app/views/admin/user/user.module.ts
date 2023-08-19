import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserRoutingModule } from './user-routing.module';
import { UserComponent } from './user.component';
import { EditUserComponent } from './edituser.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserSettingsComponent } from './usersettings.component';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { NgSelectModule } from '@ng-select/ng-select';
import { ViewUserDetail } from './modal/ViewUserDetailModal';
import { UploadUserComponent } from './modal/UploadUserModal';


@NgModule({
  imports: [
    CommonModule,
    UserRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    BsDropdownModule.forRoot(),
    ReactiveFormsModule,
    TabsModule,
    NgSelectModule,
  ],
  declarations: [
    UserComponent,
    ViewUserDetail,
    EditUserComponent,
    UserSettingsComponent,
    UploadUserComponent
  ]
})
export class UserModule { }
