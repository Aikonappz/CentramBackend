import { Component, OnInit } from "@angular/core";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { Asset } from "../../../model/Asset";
import { LoggedInUserService } from "../../../service/LoggedInUserService";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Asset Details</h6>
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
              <td><strong>Primary Details</strong></td>
              <td>
                Serial No: <span [ngClass]="{'badge':true}">{{element.serialNo}}</span><br />
                Product Category: {{element.moduleName}}<br />
                Asset Category : {{element.subModuleName}}<br />
                Model No : {{element.modelNo}}<br />
                Available : {{element.isAvailable==true? 'YES' : 'NO'}}
              </td>
            </tr>
            <tr>
              <td><strong>Reservation Details</strong></td>
              <td>
                <div *ngIf="element.isDepartment==true">
                  Department: {{element.department?.name}}<br />
                </div>
                <div *ngIf="element.isDepartment==false">
                  Organization: {{element.location?.officeName}}<br />
                </div>
                <div *ngIf="element.isLocation==true">
                  Location: {{element.raisedForLocation?.name}}
                </div>
              </td>
            </tr>
            <tr>
              <td><strong>Purchase Details</strong></td>
              <td>
                Under Warranty: {{element.isUnderWarranty==true? 'YES' : 'NO'}}<br />
                Warranty Expiry Date: {{formatDateTime(element.warrantyExpiredAt)}}<br />
                <div *ngIf="element.vendor.name!='Others'">
                  Vendor Name: {{element.vendor.name}}<br />
                </div>
                Purchase Type: {{element.purchaseType}}<br />
                <div *ngIf="element.purchaseType=='RENTED'">
                  Rental Start Date: {{formatDateTime(element.rentalStartAt)}}<br />
                  Rental End Date: {{formatDateTime(element.rentalEndAt)}}<br />
                </div>
                Available: {{element.isAvailable==true? 'YES' : 'NO'}}
                <div *ngIf="element.otherDetails!=null">
                Other Details: {{element.otherDetails}}
                </div>
              </td>
            </tr>
            <tr>
              <td><strong>Approval Details</strong></td>
              <td>
                Requested By: {{element.orderRequestedUser.employeeId}}/{{element.orderRequestedUser.firstName}} {{element.orderRequestedUser.lastName}}<br />
                Approver 1: {{element.approverUser1.employeeId}}/{{element.approverUser1.firstName}} {{element.approverUser1.lastName}}<br />
                Approver 2: {{element.approverUser2 .employeeId}}/{{element.approverUser2.firstName}} {{element.approverUser2.lastName}}
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
      //return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
      return moment.utc(d).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}