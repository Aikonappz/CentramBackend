import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { IAssignUser } from "../../../model/IAssignUser";
import { UserVO } from "../../../model/UserVO";
import { IncidentService } from "../../../service/IncidentService";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="fa fa-male"></i> Assign Incident To User</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
        <span aria-hidden="true">&times;</span>
    </button>
</div>
<div class="modal-body">
    <div class="row">
        <div class="col-sm-12">
            <div class="card ">
                <div [ngClass]="{'d-none': canAssign === true, 'card-body' : true }">
                    <div class="row">
                        <h6>Please search with module, submodule and select incident to assign an user!</h6>
                    </div>
                </div>
                <form [ngClass]="{'d-none': canAssign === false}" [formGroup]="angFormAssign"
                    (ngSubmit)="assignIncident()" novalidate>
                    <div class="card-body">
                        <div class="row">
                            <div class="col">
                                <label class="form-col-form-label required-control-label" for="assignUser">Agent</label>
                                <ng-select [items]="agentList" placeholder="-- Select Assigned User --"
                                    formControlName="assignUser" id="assignUser" name="assignUser" bindLabel="email"
                                    bindValue="id"></ng-select>
                                <div *ngIf="uf.assignUser.touched && uf.assignUser.invalid"
                                    class="alert alert-danger-custom">
                                    <div *ngIf="uf.assignUser.errors?.required">
                                        Please select agent to assign.
                                    </div>
                                </div>
                            </div>
                            <div class="col">
                                <input type="hidden" [(ngModel)]="incidents" formControlName="incidents" ngModel
                                    id="incidents" name="incidents">
                                <label class="form-col-form-label required-control-label"
                                    for="incidents">Incident Number</label>
                                <textarea formControlName="selectedIncidents"
                                    class="form-control textarea-non-resizable" id="selectedIncidents"
                                    name="selectedIncidents" readonly>
                                </textarea>
                                <div *ngIf="uf.incidents.touched && uf.incidents.invalid"
                                    class="alert alert-danger-custom">
                                    <div *ngIf="uf.incidents.errors?.required">
                                        Please select incident.
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row row-margin-05">
                            <div class="col">
                                <label class="form-col-form-label required-control-label" for="comment">Comments</label>
                                <textarea formControlName="comment" id="comment" name="comment" rows="4"
                                    class="form-control textarea-non-resizable"></textarea>
                                <div *ngIf="uf.comment.touched && uf.comment.invalid" class="alert alert-danger-custom">
                                    <div *ngIf="uf.comment.errors?.required">
                                        Comment is required!
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer">
                        <button [disabled]="!angFormAssign.valid" type="submit" class="btn btn-primary btn-sm">
                            <i class="fa fa-male"></i> Assign
                        </button>
                        <button type="button" (click)="bsModalRef.hide()" class="btn btn-danger btn-sm">
                            <i class="fa fa-close"></i> Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>`
})
export class AssignIncidentComponent implements OnInit {
    agentList: UserVO[];
    canAssign: boolean;
    selectedValues: Map<number, string>;
    assignUser: Partial<IAssignUser>;
    angFormAssign: FormGroup;
    incidents: number[] = [];
    incidentNos: string[] = [];
    selectedIncidents: string;
    constructor(
        private fb: FormBuilder,
        public bsModalRef: BsModalRef,
        private service: IncidentService,
        public options: ModalOptions,
    ) {
        this.angFormAssign = this.fb.group({
            assignUser: new FormControl(null, [
                Validators.required,
            ]),
            incidents: new FormControl(null, [
                Validators.required,
            ]),
            selectedIncidents: new FormControl(null, [
                //Validators.required,
            ]),
            comment: new FormControl(null, [
                Validators.required,
            ]),
        });
        this.assignUser = this.options.initialState.valueOf();
        //console.log((this.assignUser));
        this.assignUser.selectedValues.forEach((value: string, key: number) => {
            this.incidents.push(key);
            this.incidentNos.push(value);
        });
        this.selectedIncidents = this.incidentNos.join('\n');
        //console.log(this.incidents);
    }
    ngOnInit() {
        if (this.assignUser.agentList.length == 1) {
            this.angFormAssign.get('assignUser').setValue(this.assignUser.agentList[0].id);
        }
        this.angFormAssign.get('incidents').setValue(this.incidents);
        this.angFormAssign.get('selectedIncidents').setValue(this.selectedIncidents);
    }

    ngAfterViewInit() {
    }

    ngAfterContentInit() {
    }

    get uf() { return this.angFormAssign.controls; }

    callAssignUser(req: any) {
        this.service.assignIncidentService(req.ids, req.userId, req.comment, {})
            .subscribe((data: any) => {
                console.log("completed....");
                this.bsModalRef.hide();
            });
    }

    assignIncident() {
        if (this.angFormAssign.valid) {
            //console.log(formData);
            let assignUser = this.angFormAssign.controls['assignUser'].value;
            let incidents = this.angFormAssign.controls['incidents'].value;
            let comment = this.angFormAssign.controls['comment'].value;
            //console.log({ userId: assignUser, ids: incidents });
            this.callAssignUser({ userId: assignUser, ids: incidents, comment: comment });
        } else {
            console.log("Invalid Form!");
        }
    }
}