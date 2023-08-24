import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { tap } from "rxjs/operators";
import { AppUtility } from "../../../../config/AppUtility";
import { ReportIncidentDataSource } from "../../../../service/datasource/ReportIncidentDataSource";
import { LoggedInUserService } from "../../../../service/LoggedInUserService";
import { ReportService } from "../../../../service/ReportService";


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
          <table mat-table [dataSource]="datasource">
            <ng-container matColumnDef="incDtl">
              <th mat-header-cell *matHeaderCellDef> Incident Details </th>
              <td mat-cell *matCellDef="let element">
              <a href="javascript:void(0);" (click)="redirectTo(type, element.id)" >
                <span [ngClass]="{
                  'badge-closed': element.status=='CLOSED' && element.slaBreached == false && element.reOpened == false ,
                  'badge-sla-about-to-breach':element.status=='SLA_ABOUT_TO_BREACH' ,
                  'badge-sla-breached': element.status=='SLA_BREACHED' || element.slaBreached == true,
                  'badge-re-opened': element.reOpened == true ,  
                  'badge':true
                  }">{{element.incidentNo}}</span></a><br />
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
              <td [attr.data-label]="element.status" id="id-assigned-{{element.id}}" mat-cell *matCellDef="let element">
                {{element.assignedUser?.firstName}}&nbsp;{{element.assignedUser?.lastName}}<br />
                {{element.assignedUser?.email}}
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef> Status </th>
              <td [attr.data-label]="element.status" id="id-status-{{element.id}}" mat-cell *matCellDef="let element">
                {{element.status}}
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
          <mat-paginator showFirstLastButtons [pageSizeOptions]="[5, 10, 25, 100]" [pageSize]="5">
          </mat-paginator>
        </div>
      </div>
    </div>
  </div>`
})
export class ViewIncidentDetails implements OnInit {
  type: string;
  params: any;
  displayedColumns = ['incDtl', 'slaAt', 'assignedUser', 'status',];
  private datasource: ReportIncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: ReportService,
    public options: ModalOptions,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
  ) {
  }
  ngOnInit(): void {
    console.log(this.params);
    this.datasource = new ReportIncidentDataSource(this.service);
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

  redirectTo(type, id) {
    this.bsModalRef.hide()
    if (type == "agent" && this.params.incidentType == "INCIDENT") {
      this.router.navigate(['/incident/agent-all/edit/' + id]);
    } else if (type == "user" && this.params.incidentType == "INCIDENT") {
      this.router.navigate(['/incident/user/edit/' + id]);
    } else if (type == "agent" && this.params.incidentType == "ASSET") {
      this.router.navigate(['asset/agent-all/edit/' + id]);
    } else if (type == "user" && this.params.incidentType == "ASSET") {
      this.router.navigate(['asset/user/edit/' + id]);
    }
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}