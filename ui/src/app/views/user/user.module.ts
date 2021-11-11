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


@NgModule({
  imports: [
    CommonModule,
    UserRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
  ],
  declarations: [
    UserComponent,
    EditUserComponent,
    UserSettingsComponent
  ]
})
export class UserModule { }
