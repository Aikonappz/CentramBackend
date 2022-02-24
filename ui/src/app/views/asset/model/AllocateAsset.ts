import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { AppUtility } from "../../../config/AppUtility";
import { Asset } from "../../../model/Asset";
import { AssetRequest } from "../../../model/AssetRequest";
import { AssetType } from "../../../model/enumerator/AssetType";
import { ProductCategory } from "../../../model/enumerator/ProductCategory";
import { IAllocateAsset } from "../../../model/IAllocateAsset";
import { AssetRequestService } from "../../../service/AssetRequestService";
import { AssetService } from "../../../service/AssetService";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-male"></i> &nbsp;{{allocate? 'Allocate Asset' : 'Deallocate Asset' }}</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
      <div class="card ">
        <form [formGroup]="angFormAllocate" (ngSubmit)="allocateAsset()" novalidate>
          <div class="card-body">
            <div class="row" *ngIf="allocate">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="asset">Asset</label>
                <select class="form-control" formControlName="asset" id="asset" name="asset">
                  <option value="">-- Select Asset --</option> 
                  <option *ngFor="let e of assetList" value="{{e.id}}">
                    {{e.productCategory}}/{{e.assetType}}/{{e.modelNo}}/{{e.serialNo}}</option>
                </select>
                <div *ngIf="uf.asset.touched && uf.asset.invalid" class="alert alert-danger-custom">
                  <div *ngIf="uf.asset.errors?.required">
                    Please select asset to allocate.
                  </div>
                </div>
              </div>
            </div>
            <div class="row row-margin-05">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="comment">Comment</label>
                <textarea formControlName="comment" id="comment" name="comment" placeholder="IT Team Comment..." rows="4"
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
            <button *ngIf="allocate" [disabled]="!angFormAllocate.valid" type="button" (click)="allocateAsset(true)" class="btn btn-primary btn-sm">
              <i class="fa fa-male"></i> Allocate
            </button>
            <button *ngIf="!allocate" [disabled]="!angFormAllocate.valid" type="button" (click)="allocateAsset(false)" class="btn btn-warning btn-sm">
              <i class="fa fa-male"></i> Deallocate
            </button>
            <button [disabled]="!angFormAllocate.valid" type="button" (click)="bsModalRef.hide()" class="btn btn-danger btn-sm">
              <i class="fa fa-close"></i> Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>`
})
export class AllocateAsset implements OnInit {
  iAllocateAsset: Partial<IAllocateAsset>;
  allocate: boolean;
  assetRequest: AssetRequest;
  assetList: Asset[];
  angFormAllocate: FormGroup;
  allocationRequest: any;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private assetService: AssetService,
    private service: AssetRequestService,
    public options: ModalOptions,
  ) {
    this.iAllocateAsset = this.options.initialState.valueOf();
    this.assetRequest = this.iAllocateAsset.assetRequest;
    this.allocate = this.iAllocateAsset.allocate;
    //console.log(this.assetRequest);
    this.angFormAllocate = this.fb.group({
      comment: new FormControl("", [
        Validators.required,
      ]),
    });
    if (this.allocate) {
      this.assetService.assetsService({
        size: AppUtility.MAX_PAGE_SIZE,
        productCategory: ProductCategory[this.assetRequest.productCategory],
        assetType: AssetType[this.assetRequest.assetType],
        modelNo: this.assetRequest.modelNo,
        available: 1
      }).subscribe((data: any) => {
        this.assetList = data.content;
      });
      this.angFormAllocate = this.fb.group({
        asset: new FormControl("", [
          Validators.required,
        ]),
        comment: new FormControl("", [
          Validators.required,
        ]),
      });
    }
  }
  ngOnInit() {
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get uf() { return this.angFormAllocate.controls; }

  callAllocateAssetRequest(req: any) {
    this.service.allocateAssetRequest(req)
      .subscribe((data: any) => {
        this.bsModalRef.hide();
      });
  }

  allocateAsset(mode: boolean) {
    this.allocationRequest = {};
    if (this.angFormAllocate.valid) {
      if (this.allocate) {
        this.allocationRequest.assetId = this.angFormAllocate.controls['asset'].value;
      } else {
        this.allocationRequest.assetId = this.assetRequest.asset.id;
      }
      this.allocationRequest.feedback = this.angFormAllocate.controls['comment'].value;
      this.allocationRequest.requestId = this.assetRequest.id;
      this.allocationRequest.allocate = !mode ? false : true;
      this.callAllocateAssetRequest(this.allocationRequest);
    } else {
      console.log("Invalid Form!");
    }
  }
}