import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { LoggedInUserService } from "../../../../service/LoggedInUserService";
import { AppUtility } from "../../../../config/AppUtility";
import { ReportService } from "../../../../service/ReportService";
import { UatReportDataSource } from "../../../../service/datasource/UatReportDataSource";


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
            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Project Name</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.project.name}} [{{element.project.code}}]</div>
              </td>
            </ng-container>
            <ng-container matColumnDef="technology">
              <th mat-header-cell *matHeaderCellDef>Technology</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.technology}}</div>
  
              </td>
            </ng-container>
            <ng-container matColumnDef="module">
              <th mat-header-cell *matHeaderCellDef>Module</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.moduleName}}</div>
              </td>
            </ng-container>
            <ng-container matColumnDef="subModule">
              <th mat-header-cell *matHeaderCellDef>Sub Module</th>
              <td mat-cell *matCellDef="let element">
                <div>{{element.subModuleName}}</div>
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef>Status</th>
              <td mat-cell *matCellDef="let element">
                <span [ngClass]="{
                        'badge-closed': element.status =='Completed',                      
                        'badge-sla-breached': element.status =='Not Started',   
                        'badge-sla-about-to-breach' : element.status =='In Progress',                     
                        'badge':true
                        }">{{element.status}}</span>
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
export class ViewUATDashboardDetails implements OnInit {
    params: any;
    displayedColumns = ['name', 'technology', 'module', 'subModule', 'status',];
    datasource: UatReportDataSource;
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
        this.datasource = new UatReportDataSource(this.service);
        this.datasource.loadData(0, 5, this.params);
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