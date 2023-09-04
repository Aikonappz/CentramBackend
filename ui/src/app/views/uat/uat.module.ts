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

import { ChartsModule } from 'ng2-charts';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { NgxMaskModule } from 'ngx-mask'
import { TextMaskModule } from 'angular2-text-mask';
import { UATRoutingModule } from './uat-routing.module';
import { RemarkViewer } from './modal/RemarkViewer';
import { UATActivityComponent } from './activities/uat-activity.component';

import { UATReportComponent } from './report/uatreport.component';
import { UATDashboardComponent } from './dashboard/uatdashboard.component';
import { EditUATProjectComponent } from './master/edituatproject.component';
import { UATProjectComponent } from './master/uatproject.component';



@NgModule({
  declarations: [
    UATActivityComponent,
    RemarkViewer,
    UATDashboardComponent,
    UATReportComponent,
    UATProjectComponent,
    EditUATProjectComponent,
  ],
  imports: [
    CommonModule,
    UATRoutingModule,
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
export class UATModule { }
