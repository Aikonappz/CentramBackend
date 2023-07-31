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
import { ManageTimeSheetComponent } from './manage-timesheet.component';
import { ChartsModule } from 'ng2-charts';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { WeeklyIndividualTimeSheet } from './modal/WeeklyIndividualTimeSheet';
import { NgxMaskModule } from 'ngx-mask'
import { TextMaskModule } from 'angular2-text-mask';



@NgModule({
  declarations: [
    AllocateProjectComponent,
    DeallocateProjectComponent,
    ManageTimeSheetComponent,
    WeeklyIndividualTimeSheet,
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
    CommonModule,
    FormsModule,
    ChartsModule,
    ButtonsModule.forRoot(),
    MatTableModule,
    MatPaginatorModule,
    BsDropdownModule.forRoot(),
    TabsModule,
    TextMaskModule,
    NgxMaskModule.forRoot(),
  ]
})
export class ProjectModule { }
