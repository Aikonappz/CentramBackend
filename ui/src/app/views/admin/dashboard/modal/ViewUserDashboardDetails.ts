import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { IncidentDataSource } from "../../../../service/datasource/IncidentDataSource";
import { IncidentService } from "../../../../service/IncidentService";
import { LoggedInUserService } from "../../../../service/LoggedInUserService";
import { AppUtility } from "../../../../config/AppUtility";


@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Incidents</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
        <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="row">
        <div class="col-sm-12">
            <div class="card ">
                <table class="table table-bordered" mat-table [dataSource]="datasource">
                    <ng-container matColumnDef="inc">
                        <th mat-header-cell *matHeaderCellDef>Incident Details</th>
                        <td mat-cell *matCellDef="let element">
                            {{element.incidentNo}}<br />
                            {{element.title}}<br />
                            {{element.priority.name}}<br />
                            {{formatDateTime(element.raisedAt)}}<br />
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="slaAt">
                        <th mat-header-cell *matHeaderCellDef> SLA. Details </th>
                        <td mat-cell *matCellDef="let element">
                            {{formatDateTime(element.slaAt)}}
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="assignedUser">
                        <th mat-header-cell *matHeaderCellDef> Assigned User </th>
                        <td [attr.data-label]="element.status" mat-cell *matCellDef="let element">
                            {{element.assignedUser?.firstName}}&nbsp;{{element.assignedUser?.lastName}}<br />
                            {{element.assignedUser?.email}}
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="status">
                        <th mat-header-cell *matHeaderCellDef> Status </th>
                        <td [attr.data-label]="element.status" id="id-status-{{element.id}}" mat-cell
                            *matCellDef="let element">
                            {{element.status}}
                        </td>
                    </ng-container>
                    <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                    <tr mat-row *matRowDef="let row; let even = even; columns: displayedColumns;"
                        [ngClass]="{gray: even}"></tr>
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
export class ViewUserDashboardDetails implements OnInit {
    params: any;
    displayedColumns = ['inc', 'slaAt', 'assignedUser', 'status',];
    private datasource: IncidentDataSource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    constructor(
        private fb: FormBuilder,
        public bsModalRef: BsModalRef,
        private service: IncidentService,
        public options: ModalOptions,
        private loggedInUserService: LoggedInUserService,
    ) {
    }
    ngOnInit(): void {
        this.datasource = new IncidentDataSource(this.service);
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