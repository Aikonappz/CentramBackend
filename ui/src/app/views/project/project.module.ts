import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { CKEditorModule } from 'ckeditor4-angular';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { NgSelectModule } from '@ng-select/ng-select';
import { AllocateProjectComponent } from './allocate-project.component';
import { ProjectRoutingModule } from './asset-routing.module';
import { DeallocateProjectComponent } from './deallocate-project.component';



@NgModule({
  declarations: [
    AllocateProjectComponent,
    DeallocateProjectComponent,
  ],
  imports: [
    CommonModule,
    ProjectRoutingModule,
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
export class ProjectModule { }
