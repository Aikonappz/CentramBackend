import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChartsModule } from 'ng2-charts';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { ExploreComponent } from './explore.component';
import { ExploreRoutingModule } from './explore-routing.module';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ExploreRoutingModule,
    ChartsModule,
    ButtonsModule.forRoot(),
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    TabsModule,
  ],
  declarations: [
    ExploreComponent
  ]
})
export class ExploreModule { }
