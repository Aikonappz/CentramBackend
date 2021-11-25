import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrganisationComponent } from './organisation.component';
import { OrganisationRoutingModule } from './organisation-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { EditOrganisationComponent } from './editorganisation.component';
import { OrgSettingsComponent } from './orgsettings.component';

@NgModule({
  declarations: [
    OrgSettingsComponent,
    OrganisationComponent,
    EditOrganisationComponent
  ],
  imports: [
    CommonModule,
    OrganisationRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
  ]
})
export class OrganisationModule { }
