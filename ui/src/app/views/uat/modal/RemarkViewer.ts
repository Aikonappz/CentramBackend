import { Component, OnInit, } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { OrganisationService } from "../../../service/OrganisationService";
import { ProjectUatScriptDetail } from "../../../model/ProjectUatScriptDetail";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> Remarks </h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
      <div class="card ">
        <div class="card-body ">
          <div>
            <p *ngIf="projectUatScriptDetail.testScenarioJobId!=null&&projectUatScriptDetail.testScenarioJobId!=''">
              <b>Scenario Job Id : {{projectUatScriptDetail.testScenarioJobId}}</b>
            </p>
            <p *ngIf="projectUatScriptDetail.step!=null&&projectUatScriptDetail.step!=''"><b>Step :
                {{projectUatScriptDetail.step}}</b></p>
          </div>
          <table class="table table-bordered table-striped table-sm">
            <tr>
              <td align="center" width="25%"><strong>Name</strong></td>
              <td align="center" width="25%"><strong>Email</strong></td>
              <td align="center" width="25%"><strong>Remark</strong></td>
              <td align="center" width="25%"><strong>Date & Time</strong></td>
            </tr>
            <ng-container *ngFor="let elm of projectUatScriptDetail.remarks;let i=index;">
              <tr>
                <td>{{elm.name}}</td>
                <td>{{elm.email}}</td>
                <td>{{elm.comment}}</td>
                <td>{{formatDateTime(elm?.dateTime)}}</td>

              </tr>
            </ng-container>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>`
})
export class RemarkViewer implements OnInit {
  projectUatScriptDetail: ProjectUatScriptDetail;
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
    //console.log(this.projectUatScriptDetail);
  }

  ngAfterViewInit() {
  }

  loadData(req = {}) {
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}