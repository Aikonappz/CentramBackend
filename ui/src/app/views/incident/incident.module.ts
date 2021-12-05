import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentRoutingModule } from './incident-routing.module';
import { IncidentComponent } from './incident.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { MasterRoutingModule } from '../master/master-routing.module';
import { BrowserModule } from '@angular/platform-browser';
import { RaiseIncidentComponent } from './raiseincident.component';
import { CKEditorModule } from 'ckeditor4-angular';



@NgModule({
  declarations: [
    IncidentComponent,
    RaiseIncidentComponent,
  ],
  imports: [
    CommonModule,
    IncidentRoutingModule,
    MasterRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    CKEditorModule,
  ]
})
export class IncidentModule { }
