import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { LocationRoutingModule } from './location-routing.module';
import { LocationComponent } from './location.component';
import { EditLocationComponent } from './editlocation.component';



@NgModule({
  declarations: [
    LocationComponent,
    EditLocationComponent
  ],
  imports: [
    CommonModule,
    LocationRoutingModule,
    MatTableModule,
    MatPaginatorModule,
    FormsModule,
    ReactiveFormsModule,
    TabsModule,
  ]
})
export class LocationModule { }
