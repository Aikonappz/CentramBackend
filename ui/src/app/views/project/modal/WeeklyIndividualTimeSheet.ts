import { Component, OnInit, } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { OrganisationService } from "../../../service/OrganisationService";

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
            <table class="table table-striped">
            <thead>
              <tr>
                <td width="10%">Project ID</td>
                <td width="10%">Task/Activity ID</td>
                <td width="10%">Onsite/Offshore</td>
                <td width="10%">Billing Action</td>
                <td width="50%">
                  <table>
                    <tr>
                      <td width="15%">1</td>
                      <td width="15%">2</td>
                      <td width="15%">3</td>
                      <td width="15%">4</td>
                      <td width="15%">5</td>
                      <td width="15%">6</td>
                      <td width="15%">7</td>
                    </tr>
                    <tr>
                      <td>SAT</td>
                      <td>SUN</td>
                      <td>MON</td>
                      <td>TUE</td>
                      <td>WED</td>
                      <td>THU</td>
                      <td>FRI</td>
                    </tr>
                  </table>
                </td>
                <td width="10%">Total Hours</td>
              </tr>
            </thead>
            <tbody>
              <td>
                <select>
                  <option id="0">Proj 0</option>
                  <option id="1">Proj 1</option>
                  <option id="2">Proj 2</option>
                </select>
              </td>
              <td>
                <select>
                  <option id="0">Plan</option>
                  <option id="1">Design</option>
                  <option id="2">Development</option>
                </select>
              </td>
              <td>
                <select>
                  <option id="ON">OnShore</option>
                  <option id="OFF">OffShore</option>
                </select>
              </td>
              <td>
                <select>
                  <option id="0">Billable</option>
                  <option id="1">Non Billeable</option>
                </select>
              </td>
              <td>
                <table>
                  <tr>
                    <td width="15%">
                    <div class="col-xs-1">
                    <input class="form-control" id="inputdefault" type="text">
                    </div>
                    </td>
                    <td width="15%"><input type="text"></td>
                    <td width="15%"><input type="text"></td>
                    <td width="15%"><input type="text"></td>
                    <td width="15%"><input type="text"></td>
                    <td width="15%"><input type="text"></td>
                    <td width="15%"><input type="text"></td>
                  </tr>
                </table>
              </td>
              <td>0.00</td>
            </tbody>
          </table>
            </div>
        </div>
    </div>
  </div>`
})
export class WeeklyIndividualTimeSheet implements OnInit {
    params: any;

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
    }

    ngAfterViewInit() {
    }

    redirectTo(id) {
        this.bsModalRef.hide()
        this.router.navigate(['/organization/edit/' + id]);
    }

    loadData(req = {}) {
    }

    formatDate(d: string) {
        if (d != null && d != "") {
            return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_FORMAT);
        }
        return null;
    }
}