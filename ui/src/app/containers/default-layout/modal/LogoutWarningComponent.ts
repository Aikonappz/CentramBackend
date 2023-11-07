import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import * as moment from "moment";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { ClientStorageService } from "../../../service/ClientStorageService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="fa fa-warning"></i> Session Warning!</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="close()">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="row">
      <div class="col-sm-12">
        <div class="card ">
          <div class="card-body">
            <div class="row">
              <div class="col">
                <h6>The session will expire soon. Do you wish to continue?</h6>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <div class="float-right~">
              <button type="submit" (click)="close()" class="btn btn-warning btn-sm">
                <i class="fa fa-arrow-circle-right"></i> Continue ({{timeLeft}})
              </button>
              <a class="btn btn-danger btn-sm" href="javascript:void(0);" (click)="signOut()">
                <i class="fa fa-lock"></i> Logout
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>`,
    providers: [
        CommonModule
    ]
})
export class LogoutWarningComponent implements OnInit {
    handler: any;
    localTimerHandler: any;
    timeLeft: string;
    constructor(
        private fb: FormBuilder,
        public bsModalRef: BsModalRef,
        private router: Router,
        public options: ModalOptions,
        private clientStorageService: ClientStorageService,
    ) {
        this.handler = this.options.initialState.valueOf();
        //console.log(this.handler.handler);
        this.localTimerHandler = setInterval(() => {
            this.check();
        }, 1000);
    }
    check() {
        if (!isNaN(this.getLastAction())) {
            const now = Date.now();
            const timeleft = this.getLastAction() + AppUtility.APP_NON_ACTIVITY_LOGOUT_INTERVAL * 60 * 1000;
            const warningTimeleft = this.getLastAction() + AppUtility.APP_LOGOUT_WARNING_INTERVAL * 60 * 1000;
            const diff = timeleft - now;
            const isTimeout = diff < 0;
            if (!isTimeout) {
                var tempTime = moment.duration(diff);
                this.timeLeft = tempTime.minutes().toString().padStart(2, "0") + ":" + tempTime.seconds().toString().padStart(2, "0");
            } else {
                this.clientStorageService.set(AppUtility.APP_SESSION_TIMEOUT_KEY, "1");
                this.signOut();
            }
        }
    }
    signOut() {
        clearInterval(this.handler.handler);
        clearInterval(this.localTimerHandler);
        this.close();
        this.router.navigate(['/sign-out']);
    }
    close() {
        clearInterval(this.localTimerHandler);
        this.bsModalRef.hide();
    }
    ngOnInit() { }
    ngAfterViewInit() { }
    ngAfterContentInit() { }
    public getLastAction() {
        return parseInt(this.clientStorageService.get(AppUtility.APP_LAST_ACTION_KEY));
    }
}