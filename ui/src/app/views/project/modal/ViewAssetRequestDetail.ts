import { Component, OnInit } from "@angular/core";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { Incident } from "../../../model/Incident";
import { LoggedInUserService } from "../../../service/LoggedInUserService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Asset Request Details</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
        <span aria-hidden="true">&times;</span>
    </button>
</div>
<div class="modal-body">
    <div class="row">
        <div class="col-sm-12">
            <div class="card ">
                <table class="table table-bordered">
                    <tr>
                        <td>
                            <span [ngClass]="{
                                'badge':true
                                }">{{incident.incidentNo}}</span><br />
                            {{incident.moduleName}}<br />
                            {{incident.subModuleName}}<br />
                            {{incident.title}}<br />
                            <div *ngIf="incident.assetApproved==true">
                                Approved!
                            </div>
                            <div *ngIf="incident.assetApproved==false">
                                Not Approved!
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>`
})
export class ViewAssetRequestDetail implements OnInit {
    incident: Incident;
    constructor(
        public bsModalRef: BsModalRef,
        public options: ModalOptions,
        private loggedInUserService: LoggedInUserService,
    ) {
    }
    ngOnInit() {
    }
    ngAfterViewInit() {
    }
    ngAfterContentInit() {
    }
    formatDateTime(d: string) {
        if (d != null && d != "") {
            return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
        }
        return null;
    }
}