import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { UserDataSource } from "../../../../service/datasource/UserDataSource";
import { UserService } from "../../../../service/UserService";
import { LoggedInUserService } from "../../../../service/LoggedInUserService";
import { AppUtility } from "../../../../config/AppUtility";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Employees/Agent</h6>
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
                  <th class="w-20" mat-header-cell *matHeaderCellDef> Name </th>
                  <td mat-cell *matCellDef="let element">
                    <a href="javascript:void(0);" (click)="redirectTo(element.id)" >{{element.firstName}} {{element.lastName}}</a>
                  </td>
                </ng-container>
                <ng-container matColumnDef="email">
                  <th class="w-20" mat-header-cell *matHeaderCellDef> Email </th>
                  <td mat-cell *matCellDef="let element">
                    {{element.email}}
                  </td>
                </ng-container>
                <ng-container matColumnDef="contact">
                  <th class="w-20" mat-header-cell *matHeaderCellDef> Contact Dtl. </th>
                  <td mat-cell *matCellDef="let element">
                    <b>{{element.contactNo}}</b>
                  </td>
                </ng-container>
                <ng-container matColumnDef="categories">
                  <th class="w-20" mat-header-cell *matHeaderCellDef> Category Access </th>
                  <td mat-cell *matCellDef="let element">
                    {{element.categories.join(',')}}
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
              <mat-paginator [pageSizeOptions]="[5, 10, 25, 100]" [pageSize]="5"></mat-paginator>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>`
})
export class ViewOrgAdminDashboardUserDetails implements OnInit {
  params: any;
  displayedColumns = ['name', 'email', 'contact', 'categories',];
  private datasource: UserDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: UserService,
    private router: Router,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
  ) {
  }
  ngOnInit(): void {
    this.datasource = new UserDataSource(this.service);
    this.datasource.loadUserVOs(0, 5, this.params);
  }

  redirectTo(id) {
    this.bsModalRef.hide()
    this.router.navigate(['/user/edit/' + id]);
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
    this.datasource.loadUserVOs(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }
}