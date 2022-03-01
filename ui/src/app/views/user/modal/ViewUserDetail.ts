import { Component, OnInit } from "@angular/core";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { UserVO } from "../../../model/UserVO";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View User Details</h6>
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
                        <td>{{usr.firstName}} {{usr.lastName}}</td>
                    </tr>
                    <tr>
                        <td><strong>Email</strong></td>
                        <td>{{usr.email}}</td>
                    </tr>
                    <tr>
                        <td><strong>Contact</strong></td>
                        <td>
                        <b>{{usr.contactNo}}</b><br/>
                           {{usr.secContactNo}}
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Employee Id.</strong></td>
                        <td>{{usr.employeeId}}</td>
                    </tr>
                    <tr>
                        <td><strong>Project Code</strong></td>
                        <td>{{usr.projectCode}}</td>
                    </tr>
                    <tr>
                        <td><strong>Roles</strong></td>
                        <td>{{usr.roleNames.join(',')}}</td>
                    </tr>
                    <tr>
                        <td><strong>Location</strong></td>
                        <td>{{usr.location}} - {{usr.locationOfficeName}}</td>
                    </tr>
                    <tr>
                        <td><strong>Department</strong></td>
                        <td>{{usr.department}}</td>
                    </tr>
                    <tr>
                        <td><strong>Organization</strong></td>
                        <td>{{usr.organisation}}</td>
                    </tr>
                    <tr>
                        <td><strong>Status</strong></td>
                        <td>{{usr.status}}</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
  </div>`
})
export class ViewUserDetail implements OnInit {
    usr: UserVO;
    constructor(
        public bsModalRef: BsModalRef,
        public options: ModalOptions,
    ) {
    }
    ngOnInit() {
    }
    ngAfterViewInit() {
    }
    ngAfterContentInit() {
    }
}