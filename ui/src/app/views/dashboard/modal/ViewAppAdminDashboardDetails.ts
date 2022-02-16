import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { AppUtility } from "../../../config/AppUtility";
import { OrganisationDataSource } from "../../../service/datasource/OrganisationDataSource";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { OrganisationService } from "../../../service/OrganisationService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Organization</h6>
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
                        <th mat-header-cell *matHeaderCellDef> Name </th>
                        <td mat-cell *matCellDef="let element">
                            <a href="javascript:void(0);" (click)="redirectTo(element.id)" >{{element.name}}</a>                            
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="addressDtl">
                        <th mat-header-cell *matHeaderCellDef> Address </th>
                        <td mat-cell *matCellDef="let element">
                            {{element.add1}}<br />
                            {{element.add2}}<br />
                            {{element.city}}<br />
                            {{element.pincode}}
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="licence">
                        <th mat-header-cell *matHeaderCellDef> Licence Dtl. </th>
                        <td mat-cell *matCellDef="let element">
                            {{element.licenseType}}<br />
                            {{formatDate(element.licenseStart)}}<br />
                            {{formatDate(element.licenseEnd)}}
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
                <mat-paginator [pageSizeOptions]="[5, 10, 25, 100]" [pageSize]="5"></mat-paginator>
            </div>
        </div>
    </div>
  </div>`
})
export class ViewAppAdminDashboardDetails implements OnInit {
    params: any;
    displayedColumns = ['name', 'addressDtl', 'licence', 'status'];
    private datasource: OrganisationDataSource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    constructor(
        private fb: FormBuilder,
        public bsModalRef: BsModalRef,
        private router: Router,
        private service: OrganisationService,
        public options: ModalOptions,
        private loggedInUserService: LoggedInUserService,
    ) {
    }
    ngOnInit(): void {
        this.datasource = new OrganisationDataSource(this.service);
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

    redirectTo(id) {
        this.bsModalRef.hide()
        this.router.navigate(['/organization/edit/' + id]);
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
}