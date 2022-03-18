import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AssetApprovalDTO } from "../../../model/AssetApprovalDTO";
import { Incident } from "../../../model/Incident";
import { IncidentService } from "../../../service/IncidentService";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-male"></i> &nbsp; Deallocate Asset</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
      <div class="card ">
        <form [formGroup]="angForm" (ngSubmit)="allocateAsset()" novalidate>
          <div class="card-body">
            <div class="row row-margin-05">
              <div class="col">
                <label class="form-col-form-label" for="assetDetai">Asset Detail</label>
                <input type="text" id="assetDetai" readonly value="{{incident.asset.modelNo}}/{{incident.asset.serialNo}}"
                  class="form-control">
              </div>
            </div>
            <div class="row row-margin-05">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="comment">Comment</label>
                <textarea formControlName="comment" id="comment" name="comment" placeholder="Deallocation Comment..."
                  rows="4" class="form-control textarea-non-resizable"></textarea>
                <div *ngIf="uf.comment.touched && uf.comment.invalid" class="alert alert-danger-custom">
                  <div *ngIf="uf.comment.errors?.required">
                    Comment is required!
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <button [disabled]="!angForm.valid" type="button" (click)="allocateAsset(incident.id)"
              class="btn btn-primary btn-sm"><i class="fa fa-check-circle"></i> Allocate
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
export class DeallocateAsset implements OnInit {
  incident: Incident;
  angForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: IncidentService,
    public options: ModalOptions,
  ) {
    this.angForm = this.fb.group({
      comment: new FormControl("", [
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

  get uf() { return this.angForm.controls; }

  allocateAsset(id: string) {
    console.log(id,);
    if (this.angForm.valid) {
      this.service
        .deallocateAssetRequest({ requestId: id, feedback: this.angForm.controls['comment'].value })
        .subscribe((data: any) => {
          this.bsModalRef.hide();
        });
    } else {
      console.log("Invalid Form!");
    }
  }

}