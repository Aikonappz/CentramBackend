import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="fa fa-info"></i>&nbsp;&nbsp;Info</h6>
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
                <h6>{{msg}}</h6>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <div class="float-right~">
              <button type="submit" (click)="close()" class="btn btn-warning btn-sm">
                Close
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
export class CommonAlert implements OnInit {
  msg: string;
  url: string;
  constructor(
    public bsModalRef: BsModalRef,
    private router: Router,
    public options: ModalOptions,
  ) {

  }
  close() {
    this.bsModalRef.hide();
    this.router.navigate([this.url]);
  }
  ngOnInit() { }
  ngAfterViewInit() { }
  ngAfterContentInit() { }
}