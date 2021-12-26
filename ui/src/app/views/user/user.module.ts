import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserRoutingModule } from './user-routing.module';
import { UserComponent, UserUploadComponent, ViewUserDetail } from './user.component';
import { EditUserComponent } from './edituser.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserSettingsComponent } from './usersettings.component';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { RouterModule } from '@angular/router';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';


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
  ],
  declarations: [
    UserComponent,
    ViewUserDetail,
    EditUserComponent,
    UserSettingsComponent,
    UserUploadComponent
  ]
})
export class UserModule { }
