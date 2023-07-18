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
        <table>
          <tr>
            <td width="25%">Project</td>
            <td width="25%">Task</td>
            <td width="25%">On/Off</td>
            <td width="25%">Billing</td>
          </tr>
          <tr>
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
          </tr>
         </table> 
         
         <table> 
          
          <tr>
            <td>
              <table border="1">
                <tr>
                  <td style="text-align: center;">MON</td>
                  <td style="text-align: center;">TUE</td>
                  <td style="text-align: center;">WED</td>
                  <td style="text-align: center;">THU</td>
                  <td style="text-align: center;">FRI</td>
                  <td style="text-align: center;">SAT</td>
                  <td style="text-align: center;">SUN</td>
                </tr>
                <tr>
                  <td><input style="width: 100%" type="text"></td>
                  <td><input style="width: 100%" type="text"></td>
                  <td><input style="width: 100%" type="text"></td>
                  <td><input style="width: 100%" type="text"></td>
                  <td><input style="width: 100%" type="text"></td>
                  <td><input style="width: 100%" type="text"></td>
                  <td><input style="width: 100%" type="text"></td>
                </tr>
              </table>
            </td>
          </tr>
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