import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TimesheetReportComponent } from './report/timesheetreport.component';

import { UATActivityComponent } from './activities/uat-activity.component';
import { TimeSheetDashboardComponent } from './dashboard/dashboard.component';
import { UATProjectComponent } from './master/uatproject.component';
import { EditUATProjectComponent } from './master/edituatproject.component';
import { EditLocationComponent } from './master/editlocation.component';
import { LocationComponent } from './master/location.component';
import { EditDepartmentComponent } from './master/editdepartment.component';
import { DepartmentComponent } from './master/department.component';
import { EditAccountComponent } from './master/editaccount.component';
import { AccountComponent } from './master/account.component';
import { AllocateProjectComponent } from './operation/allocate-project.component';
import { DeallocateProjectComponent } from './operation/deallocate-project.component';
import { AddTimeSheetComponent, } from './manage-timesheet/add-timesheet.component';
import { TimeSheetsComponent } from './manage-timesheet/timesheets.component';
import { TimeSheetApprovalListComponent } from './operation/timesheet-approval-list.component';
import { TimeSheetApprovalComponent } from './operation/timesheet-approval.component';
import { EditTimeSheetComponent } from './manage-timesheet/edit-timesheet.component';


const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Time Sheet'
    },
    children: [
      {
        path: '',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: TimeSheetDashboardComponent,
        pathMatch: 'full',
        data: {
          title: 'Timesheet Dashboard'
        },
      },
      {
        path: 'operation/allocate',
        component: AllocateProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Allocate Project'
        },
      },
      {
        path: 'operation/deallocate',
        component: DeallocateProjectComponent,
        pathMatch: 'full',
        data: {
          title: 'Deallocate Project'
        }
      },
      {
        path: 'operation/timesheet-approval-list',
        component: TimeSheetApprovalListComponent,
        pathMatch: 'full',
        data: {
          title: 'Timesheet Approval List'
        }
      },
      {
        path: 'operation/timesheet-approval/:id',
        component: TimeSheetApprovalComponent,
        pathMatch: 'full',
        data: {
          title: 'Timesheet Approval'
        }
      },
      {
        path: 'timesheet',
        component: TimeSheetsComponent,
        pathMatch: 'full',
        data: {
          title: 'Submitted TimeSheets'
        }
      },
      {
        path: 'timesheet/add',
        component: AddTimeSheetComponent,
        pathMatch: 'full',
        data: {
          title: 'Add TimeSheet'
        }
      },
      {
        path: 'timesheet/edit/:id',
        component: EditTimeSheetComponent,
        pathMatch: 'full',
        data: {
          title: 'Edit TimeSheet'
        }
      },
      {
        path: 'report',
        component: TimesheetReportComponent,
        pathMatch: 'full',
        data: {
          title: 'Timesheet Report'
        },
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TimeSheetRoutingModule { }
