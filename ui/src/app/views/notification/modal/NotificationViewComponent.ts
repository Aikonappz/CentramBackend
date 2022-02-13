import { Component, OnInit } from "@angular/core";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { Status } from "../../../model/enumerator/Status";
import { Notification } from "../../../model/Notification";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { MiscService } from "../../../service/MiscService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="fa fa-info-circle"></i>&nbsp; &nbsp;{{notification.notificationTitle}}</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
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
                            <div [innerHTML]="notification.notificationBody"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>`
})
export class NotificationViewComponent implements OnInit {
    notification: Notification;
    data: any;
    constructor(
        public options: ModalOptions,
        public bsModalRef: BsModalRef,
        public service: MiscService,
        private loggedInUserService: LoggedInUserService,
    ) {
        this.data = this.options.initialState.valueOf();;
        //console.log(this.data);
        if (this.data.notification.status != "VISITED") {
            let updatedNotifications = [];
            updatedNotifications.push(this.data.notification.id);
            this.service
                .updateNotificationsStatusService(updatedNotifications, Status.VISITED)
                .subscribe((data: any) => {
                });
        }
    }
    ngOnInit() { }
    ngAfterViewInit() { }
    ngAfterContentInit() { }
}