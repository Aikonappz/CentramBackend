import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { IncidentService } from "../../../service/IncidentService";
import { TimeEntry } from "../../../model/TimeEntry";
import { AppUtility } from "../../../config/AppUtility";
import { ClientStorageService } from "../../../service/ClientStorageService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="cil-av-timer"></i> Time Entries </h6>
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
                        <table class="table table-striped" width="100%">
                            <tr>
                                <th colspan="3" width="100%">Time Entries - {{incidentNo}} </th>
                            </tr>
                            <tr>
                                <th>Purpose</th>
                                <th>Time Spent</th>
                            </tr>
                            <tr *ngIf="timeEntries.length==0">
                                <td colspan="3" style="text-align: center;">No Time Entry Yet!</td>
                            </tr>
                            <ng-container *ngFor="let timeEntry of timeEntries;let i=index;">
                                <tr>
                                    <td>{{timeEntry.purpose}}</td>
                                    <td>{{timeEntry.time}} min</td>
                                </tr>
                            </ng-container>
                        </table>
                    </div>
                </div>
                <div class="card-footer">
                </div>
            </div>
        </div>
    </div>
</div>`
})
export class ViewTimeEntryComponent implements OnInit {
    timeEntries: TimeEntry[];
    incidentNo: string;
    incidentId: number;
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

    close() {
        this.bsModalRef.hide();
    }
}