import { Component, OnInit } from "@angular/core";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { Organisation } from "../../../model/Organisation";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Organisation Details</h6>
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
                        <td><strong>Name</strong></td>
                        <td>{{org.name}}</td>
                    </tr>
                    <tr>
                        <td><strong>Address Line 1</strong></td>
                        <td>{{org.add1}}</td>
                    </tr>
                    <tr>
                        <td><strong>Address Line 2</strong></td>
                        <td>{{org.add2}}</td>
                    </tr>
                    <tr>
                        <td><strong>City</strong></td>
                        <td>{{org.city}}</td>
                    </tr>
                    <tr>
                        <td><strong>Pincode</strong></td>
                        <td>{{org.pincode}}</td>
                    </tr>
                    <tr>
                        <td><strong>PAN</strong></td>
                        <td>{{org.pan}}</td>
                    </tr>
                    <tr>
                        <td><strong>TAN</strong></td>
                        <td>{{org.tan}}</td>
                    </tr>
                    <tr>
                        <td><strong>GSTIN</strong></td>
                        <td>{{org.gstin}}</td>
                    </tr>
                    <tr>
                        <td><strong>License Type</strong></td>
                        <td>{{org.licenseType}}</td>
                    </tr>
                    <tr>
                        <td><strong>License Start</strong></td>
                        <td>{{formatDate(org.licenseStart)}}</td>
                    </tr>
                    <tr>
                        <td><strong>License End</strong></td>
                        <td>{{formatDate(org.licenseEnd)}}</td>
                    </tr>
                    <tr>
                        <td><strong>Contact Persons</strong></td>
                        <td>
                            <div
                                *ngIf="org.contactPersons.length > 0 && (org.contactPersons[0].name != null && org.contactPersons[0].name != '')">
                                <p>Key person 1 name - {{org.contactPersons[0].name}}</p>
                                <p>Key person 1 email - {{org.contactPersons[0].email}}</p>
                                <p>Key person 1 contact - {{org.contactPersons[0].contactNo}}</p>
                            </div>
                            <div
                                *ngIf="org.contactPersons.length > 1 && (org.contactPersons[1].name != null && org.contactPersons[1].name != '')">
                                <hr />
                                <p>Key person 2 name - {{org.contactPersons[1].name}}</p>
                                <p>Key person 2 email - {{org.contactPersons[1].email}}</p>
                                <p>Key person 2 contact - {{org.contactPersons[1].contactNo}}</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Status</strong></td>
                        <td>{{org.status}}</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>`
})
export class OrganisationDetailModal implements OnInit {
    org: Organisation;
    constructor(public bsModalRef: BsModalRef, public options: ModalOptions,) { }
    ngOnInit() { }
    ngAfterViewInit() { }
    ngAfterContentInit() { }
    formatDate(d: string) {
        if (d != null && d != "") {
            return moment.utc(d).tz(AppUtility.APP_DEFAULT_TIMEZONE).format(AppUtility.APP_VIEW_DATE_FORMAT);
        }
        return null;
    }
}