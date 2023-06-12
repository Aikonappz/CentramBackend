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
            <form [formGroup]="angForm" (ngSubmit)="formSubmit()" novalidate>
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
                                    <th>Action</th>
                                </tr>
                                <tr *ngIf="timeEntries.length==0">
                                    <td colspan="3" style="text-align: center;">No Time Entry Yet!</td>
                                </tr>
                                <ng-container *ngFor="let timeEntry of timeEntries;let i=index;">
                                    <tr>
                                        <td>{{timeEntry.purpose}}</td>
                                        <td>{{timeEntry.time}}</td>
                                        <td>
                                            <div *ngIf="timeEntry.newEntry==true">
                                                <button (click)="remove(i)" type="button"
                                                    class=" btn btn-secondary btn-sm remove"><i
                                                        class="fa fa-minus-circle" aria-hidden="true"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </ng-container>
                            </table>
                        </div>
                        <div class="row">
                            <div class="col">
                                <label class="form-col-form-label" for="statusReadOnly">Purpose</label>
                                <input type="text" formControlName="purpose" id="purpose" name="purpose"
                                    class="form-control" placeholder="Purpose" aria-label="Incident Number">
                                <div *ngIf="f.purpose.touched && f.purpose.invalid" class="alert alert-danger-custom">
                                    <div *ngIf="f.purpose.errors?.required">
                                        Purpose is required.
                                    </div>
                                </div>
                            </div>
                            <div class="col">
                                <label class="form-col-form-label" for="statusReadOnly">Time</label>
                                <ng-select [items]="timeList" placeholder="-- Select Time --" formControlName="time"
                                    id="time" name="time" bindLabel="label" bindValue="id"></ng-select>
                                <div *ngIf="f.time.touched && f.time.invalid" class="alert alert-danger-custom">
                                    <div *ngIf="f.time.errors?.required">
                                        Time is required.
                                    </div>
                                </div>
                            </div>
                            <div class="col">
                                <label class="form-col-form-label" for="statusReadOnly">Time</label>
                                <div>
                                    <button [disabled]="!angForm.valid" type="submit" class="btn btn-primary"><i
                                            class="fa fa-plus-circle" aria-hidden="true"></i>&nbsp; Add
                                        Entry</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer">
                        <button (click)="saveTimeEntry()" type="button" class="btn btn-primary btn-sm">
                             Save
                        </button>
                        <!--<button type="button" (click)="close()" class="btn btn-danger btn-sm">
                            <i class="fa fa-close"></i> Cancel
                        </button>-->
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>`
})
export class AddTimeEntryComponent implements OnInit {
    timeEntries: TimeEntry[];
    incidentNo: string;
    incidentId: number;
    timeList: any[] = [];
    angForm: FormGroup;
    constructor(
        private fb: FormBuilder,
        public bsModalRef: BsModalRef,
        private service: IncidentService,
        public options: ModalOptions,
        private clientStorageService: ClientStorageService,
    ) {
        let tmList = AppUtility.getSlaList(79);
        for (let k = 0; k < tmList.length; k++) {
            if (k != 0) {
                this.timeList.push({ id: tmList[k], label: tmList[k] + " hrs" });
            }
        }
        this.angForm = this.fb.group({
            purpose: new FormControl(null, [
                Validators.required,
            ]),
            time: new FormControl(null, [
                Validators.required,
            ]),
        });
    }
    ngOnInit() {
    }

    ngAfterViewInit() {
    }

    ngAfterContentInit() {
    }

    get f() { return this.angForm.controls; }

    close() {
        // let t = [];
        // for (let i = 0; i < this.timeEntries.length; i++) {
        //     if (!this.timeEntries[i].newEntry) {
        //         t.push(this.timeEntries[i]);
        //     }
        // }
        // this.clientStorageService.set(this.incidentId.toString(), JSON.stringify(t));
        this.bsModalRef.hide();
    }

    saveTimeEntry() {
        this.clientStorageService.set(this.incidentId.toString(), JSON.stringify(this.timeEntries));
        this.bsModalRef.hide();
    }

    formSubmit() {
        if (this.angForm.valid) {
            this.timeEntries = this.timeEntries != null && this.timeEntries.length > 0 ? this.timeEntries : [];
            this.timeEntries.push({
                "purpose": this.angForm.controls['purpose'].value,
                "time": this.angForm.controls['time'].value,
                "newEntry": true,
            });
            //console.log(this.timeEntries);
            this.angForm.reset();
        } else {
            console.log("Invalid Form!");
        }
    }

    remove(index) {
        this.timeEntries.splice(index, 1);
    }

}