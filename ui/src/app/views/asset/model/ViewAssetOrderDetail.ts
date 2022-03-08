import { Component, OnInit } from "@angular/core";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { AssetOrder } from "../../../model/AssetOrder";
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
                        <td><strong>Order Details</strong></td>
                        <td>
                            <span [ngClass]="{
                            'badge-sla-about-to-breach':assetOrder.approvedUser1==false ,
                            'badge-closed':assetOrder.approvedUser1==true ,
                            'badge':true
                            }">{{assetOrder.orderNo}}</span><br />
                            <div *ngIf="assetOrder.isDepartment==true">
                                Department: {{assetOrder.department?.name}}<br />
                            </div>
                            <div *ngIf="assetOrder.isDepartment==false">
                                Organization: {{assetOrder.location?.officeName}}<br />
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Asset Details</strong></td>
                        <td>
                            Product Category: {{assetOrder.moduleName}}<br />
                            Asset Category: {{assetOrder.subModuleName}}<br />
                            Model: {{assetOrder.model}}<br />
                            Quantity: {{assetOrder.quantity}}<br />
                            <div *ngIf="assetOrder.withinBudget==true">
                                With In Budget: Yes
                            </div>
                            <div *ngIf="assetOrder.withinBudget==false">
                                With In Budget: No<br />
                                Limit Amount: {{assetOrder.limitAmount}}<br />
                                Extra Amount: {{assetOrder.extraAmount}}
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Vendor Details</strong></td>
                        <td>
                            Name: {{assetOrder.vendor.name}}<br />
                            Purchase Type: {{assetOrder.purchaseType}}<br />
                            <div *ngIf="assetOrder.purchaseType=='RENTED'">
                                Rent Start Date: {{formatDateTime(assetOrder.rentStartAt)}}<br />
                                Rent End Date: {{formatDateTime(assetOrder.rentEndAt)}}<br />
                            </div>
                            <div *ngIf="assetOrder.existingAgreement==true">
                                Existing Agreement: Yes
                                Agreement End Date: {{formatDateTime(assetOrder.agreementEndAt)}}<br />
                            </div>
                            <div *ngIf="assetOrder.existingAgreement==false">
                                Existing Agreement: No
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Approver Details</strong></td>
                        <td>
                            <span [ngClass]="{
                                'badge-sla-about-to-breach':assetOrder.approvedUser1==false ,
                                'badge-closed':assetOrder.approvedUser1==true ,
                                'badge':true
                                }">
                                {{assetOrder.approverUser1.firstName}} {{assetOrder.approverUser1.lastName}}
                            </span><br />
                            <span [ngClass]="{
                                'badge-sla-about-to-breach':assetOrder.approvedUser2==false ,
                                'badge-closed':assetOrder.approvedUser2==true ,
                                'badge':true
                                }">
                                {{assetOrder.approverUser2.firstName}} {{assetOrder.approverUser2.lastName}}
                            </span>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>`
})
export class ViewAssetOrderDetail implements OnInit {
    assetOrder: AssetOrder;
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