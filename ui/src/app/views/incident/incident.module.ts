import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentRoutingModule } from './incident-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { MasterRoutingModule } from '../master/master-routing.module';
import { EditIncidentComponent } from './editincident.component';
import { CKEditorModule } from 'ckeditor4-angular';
import { AssignIncidentComponent, AgentIncidentComponent } from './agentincident.component';
import { UserIncidentComponent } from './userincident.component';



@NgModule({
  declarations: [
    UserIncidentComponent,
    EditIncidentComponent,
    AgentIncidentComponent,
    AssignIncidentComponent
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
