import { Component, OnInit } from "@angular/core";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { Asset } from "../../../model/Asset";
import { LoggedInUserService } from "../../../service/LoggedInUserService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Asset Order Details</h6>
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
                        <td><strong>Asset Details</strong></td>
                        <td>
                            <span [ngClass]="{'badge':true}">{{element.serialNo}}</span><br />
                            Product Category: {{element.productCategory}}<br />
                            Asset Category : {{element.assetType}}<br />
                            Model No : {{element.modelNo}}<br />
                            Under Warranty: {{element.isUnderWarranty==true? 'YES' : 'NO'}}<br />
                            Warranty Expired Date: {{formatDateTime(element.warrantyExpiredAt)}}<br />
                            Available : {{element.isAvailable==true? 'YES' : 'NO'}}
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Location/Office Details</strong></td>
                        <td>
                            <div *ngIf="element.isDepartment==true">
                                Department: {{element.department?.name}}<br />
                            </div>
                            <div *ngIf="element.isDepartment==false">
                                Organization: {{element.location?.officeName}}<br />
                            </div>
                            Raised For Location: {{element.raisedForLocation?.name}}<br />
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Vendor Details</strong></td>
                        <td>
                            Name: {{element.vendor.name}}<br />
                            Purchase Type: {{element.purchaseType}}<br />
                            <div *ngIf="element.purchaseType=='RENTED'">
                                Rental Start Date: {{formatDateTime(element.rentalStartAt)}}<br />
                                Rental End Date: {{formatDateTime(element.rentalEndAt)}}<br />
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>`
})
export class ViewAssetDetail implements OnInit {
    element: Asset;
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