import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { LoggedInUserService } from "../../../../service/LoggedInUserService";
import { AppUtility } from "../../../../config/AppUtility";
import { ReportService } from "../../../../service/ReportService";
import { TimesheetReportDataSource } from "../../../../service/datasource/TimesheetReportDataSource";


@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View UAT Details</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="row">
      <div class="col-sm-12">
        <div class="card ">
          <table mat-table [dataSource]="datasource">
            <ng-container matColumnDef="user">
              <th mat-header-cell *matHeaderCellDef>User</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.userName}} <{{element.userEmail}}></div>
                <div>{{element.userEmpId}}</div>
              </td>
            </ng-container>
            <ng-container matColumnDef="project">
              <th mat-header-cell *matHeaderCellDef>Project</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.projectName}} <{{element.projectCode}}></div>
                <div>{{element.task}}</div>
                <div>{{element.taskDescription}}</div>
              </td>
            </ng-container>
            <ng-container matColumnDef="work">
              <th mat-header-cell *matHeaderCellDef>Work Detail</th>
              <td mat-cell *matCellDef="let element">
                <div>Billable : {{element.billable? 'YES' : 'NO'}}</div>
                <div>Hours : {{element.totalHours}}</div>
                <div>Status : {{element.approved? 'Approved' : 'Pending'}}</div>
              </td>
            </ng-container>
            <ng-container matColumnDef="approver">
              <th mat-header-cell *matHeaderCellDef>Approver</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.approverName}} <{{element.approverEmail}}></div>
                <div>{{element.approverEmpId}}</div>
                <div>{{element.approverComment}}</div>
              </td>
            </ng-container>            
            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; let even = even; columns: displayedColumns;" [ngClass]="{gray: even}"></tr>
            <tr class="mat-row" *matNoDataRow>
              <td class="mat-cell" align="center" colspan="9999">
                No data found!
              </td>
            </tr>
          </table>
          <mat-paginator showFirstLastButtons [pageSizeOptions]="[ 10, 25, 100]" [pageSize]="10">
          </mat-paginator>
        </div>
      </div>
    </div>
  </div>`
})
// <div><a target="_blank" href="uat/activities" >{{element.project.name}} [{{element.project.code}}]</a></div>
export class ViewTimesheetDashboardDetails implements OnInit {
  params: any;
  displayedColumns = ['user', 'project', 'work', 'approver',];
  datasource: TimesheetReportDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
    private service: ReportService,
  ) {
  }
  ngOnInit(): void {
    this.datasource = new TimesheetReportDataSource(this.service);
    this.datasource.loadData(0, 10, this.params);
  }

  ngAfterViewInit() {
    this.datasource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();
    this.paginator.page
      .pipe(
        tap(() => this.loadData(this.params))
      )
      .subscribe();
  }

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }
  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}