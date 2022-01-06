import { Component, OnInit, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { DashboardService } from '../../service/DashboardService';
import { AdminDashboard } from '../../model/AdminDashboard';
import { FormBuilder } from '@angular/forms';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { OrganisationService } from '../../service/OrganisationService';
import { OrganisationDataSource } from '../../service/datasource/OrganisationDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';

@Component({
  templateUrl: 'dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  role: string;
  loggedInUser: LoggedInUser;
  adminDashboard: AdminDashboard;
  modalRef: BsModalRef;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private service: DashboardService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    if (this.loggedInUser.appManager === true) {
      this.role = "APP_ADMIN";
      this.adminDashboard = new AdminDashboard();
    }
  }

  getTitle(state, parent) {
    var data = [];
    if (parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }
    if (state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    if (this.role == 'APP_ADMIN') {
      this.service
        .appAdminDashboard()
        .subscribe((data: AdminDashboard) => {
          this.adminDashboard = data;
        });
    } else {

    }
  }

  view(element: any) {
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
    this.modalRef = this.modalService.show(ViewAppAdminDashboardDetails,
      Object.assign({}, config, { initialState })
    );
  }

}

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
                          {{element.name}}
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
              <mat-paginator [pageSizeOptions]="[ 10, 25, 100]" [pageSize]="10"></mat-paginator>
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
