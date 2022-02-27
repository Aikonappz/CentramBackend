import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrganisationComponent, } from './organisation.component';
import { OrganisationRoutingModule } from './organisation-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { EditOrganisationComponent } from './edit-organisation.component';
import { OrgSettingsComponent } from './orgsettings.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { NgSelectModule } from '@ng-select/ng-select';
import { OrganisationDetailModal } from './modal/OrganisationDetailModal';

@NgModule({
  declarations: [
    OrgSettingsComponent,
    OrganisationDetailModal,
    OrganisationComponent,
    EditOrganisationComponent
  ],
  imports: [
    CommonModule,
    OrganisationRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
    NgSelectModule,
  ]
})
export class OrganisationModule { }
