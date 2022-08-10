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
import { UserIncidentComponent } from './userincident.component';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { AssignedIncidentComponent } from './assignedincident.component';
import { AssignIncidentComponent } from './modal/AssignIncidentComponent';
import { AgentIncidentComponent } from './agentincident.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { IncidentComponent } from './incident.component';

@NgModule({
  declarations: [
    UserIncidentComponent,
    EditIncidentComponent,
    AgentIncidentComponent,
    AssignIncidentComponent,
    AssignedIncidentComponent,
    IncidentComponent,
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
    TooltipModule.forRoot(),
    BsDropdownModule.forRoot(),
    NgSelectModule,
  ]
})
export class IncidentModule { }
