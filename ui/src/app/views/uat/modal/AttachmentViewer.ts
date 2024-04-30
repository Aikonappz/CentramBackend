import { Component, OnInit, } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { OrganisationService } from "../../../service/OrganisationService";
import { ProjectUatScriptDetail } from "../../../model/ProjectUatScriptDetail";
import { MediaService } from "../../../service/MediaService";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> Attachments </h6>
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
              <td align="center" width="25%"><strong>User</strong></td>
              <td align="center" width="25%"><strong>Email</strong></td>
              <td align="center" width="25%"><strong>File</strong></td>
            </tr>
            <ng-container *ngFor="let elm of projectUatScriptDetail.attachments;let i=index;">
              <tr>
                <td>
                  <div>{{elm.user?.firstName}} {{elm.user?.lastName}}</div>
                  <div>{{elm.user?.email}}</div>
                </td>
                <td>{{formatDateTime(elm.uploadedAt)}}</td>
                <td><span class="actionable-span" (click)="downloadFile(elm.id, elm.fileName)">{{elm.fileName}}&nbsp;&nbsp;</span></td>
              </tr>
            </ng-container>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>`
})
export class AttachmentViewer implements OnInit {
  projectUatScriptDetail: ProjectUatScriptDetail;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private router: Router,
    private service: OrganisationService,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
    private mediaService: MediaService,
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

  /**
  * 
  * @param idFile 
  * @param fileName 
  * @returns 
  */
  downloadFile(idFile: number, fileName: string) {
    this.mediaService
      .downloadMediaService(idFile, {})
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: data.type });
        const file = new File([blob], fileName, { type: data.type });
        let url = window.URL.createObjectURL(file);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          //alert('Please disable your Pop-up blocker and try again.');
        }
      });
    return false;
  }
}