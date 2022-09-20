import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationComponent, } from './notification.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { NotificationRoutingModule } from './notification-routing.module';
import { NotificationViewComponent } from './modal/NotificationViewComponent';
import { SafePipe } from '../../service/pipe/SafePipe';

@NgModule({
  declarations: [
    NotificationComponent,
    NotificationViewComponent,
    SafePipe,
  ],
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    NotificationRoutingModule,
  ]
})
export class NotificationModule { }
