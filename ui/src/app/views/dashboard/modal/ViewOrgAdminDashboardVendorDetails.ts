import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import * as moment from "moment";
import { BsModalRef, BsModalService, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { AppUtility } from "../../../config/AppUtility";
import { VendorDataSource } from "../../../service/datasource/VendorDataSource";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { MiscService } from "../../../service/MiscService";
import { ViewOrgAdminDashboardUserDetails } from "./ViewOrgAdminDashboardUserDetails";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Vendors</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="row">
      <div class="col-sm-12">
        <div class="card ">
          <div class="row">
            <div class="col-sm-12">
              <table mat-table [dataSource]="datasource">
                <ng-container matColumnDef="name">
                  <th mat-header-cell *matHeaderCellDef> Vendor Name </th>
                  <td mat-cell *matCellDef="let element">
                    <a href="javascript:void(0);" (click)="viewOrgAdminUserDetail({vendorId: element.id })"> {{element.name}} </a>

                  </td>
                </ng-container>
                <ng-container matColumnDef="allocationType">
                  <th mat-header-cell *matHeaderCellDef> Incident Allocation Type </th>
                  <td mat-cell *matCellDef="let element">
                    {{element.ticketAllocationType}}
                  </td>
                </ng-container>
                <ng-container matColumnDef="status">
                  <th mat-header-cell *matHeaderCellDef> Status </th>
                  <td mat-cell *matCellDef="let element">
                    {{element.status}}
                  </td>
                </ng-container>
                <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                <tr mat-row *matRowDef="let row; let even = even; columns: displayedColumns;" [ngClass]="{gray: even}">
                </tr>
                <tr class="mat-row" *matNoDataRow>
                  <td class="mat-cell" align="center" colspan="9999">
                    No data found!
                  </td>
                </tr>
              </table>
              <mat-paginator showFirstLastButtons [pageSizeOptions]="[5, 10, 25, 100]" [pageSize]="5">
              </mat-paginator>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>`
})
export class ViewOrgAdminDashboardVendorDetails implements OnInit {
  params: any;
  displayedColumns = ['name', 'allocationType', 'status'];
  private datasource: VendorDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: MiscService,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
    private modalService: BsModalService,
  ) {
  }
  ngOnInit(): void {
    this.datasource = new VendorDataSource(this.service);
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

  viewOrgAdminUserDetail(element: any) {
    this.bsModalRef.hide();
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-xl',
    };
    const initialState = {
      params: element
    };
    this.bsModalRef = this.modalService.show(ViewOrgAdminDashboardUserDetails,
      Object.assign({}, config, { initialState })
    );
  }
}