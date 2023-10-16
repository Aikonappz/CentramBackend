import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { Subject } from "rxjs/internal/Subject";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="fa fa-info"></i>&nbsp;&nbsp;Confirmation</h6>
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
                <h6><div [innerHtml]="msg"></div></h6>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <div class="float-right~">
              <button type="button" (click)="yes()" class="btn btn-primary btn-sm">
                Yes
              </button>          
              <button type="button" (click)="no()" class="btn btn-warning btn-sm">
                No
              </button>      
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
export class ConfirmAlert implements OnInit {
  msg: string;
  url: string;
  public onClose: Subject<boolean>;
  constructor(
    public bsModalRef: BsModalRef,
    private router: Router,
    public options: ModalOptions,
  ) {

  }
  close() {
    this.bsModalRef.hide();
  }
  ngOnInit() { this.onClose = new Subject(); }
  ngAfterViewInit() { }
  ngAfterContentInit() { }

  yes() {
    this.onClose.next(true);
    this.bsModalRef.hide();
  }

  no() {
    this.onClose.next(false);
    this.bsModalRef.hide();
  }

}