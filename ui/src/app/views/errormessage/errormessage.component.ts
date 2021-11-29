import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { ErrorService } from '../../service/ErrorService';

@Component({
  selector: 'app-errormessage',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-info-circle"></i> {{title}}</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
        {{message}}
    </div>
  </div>
</div>`,
  styleUrls: ['./errormessage.component.scss']
})
export class ErrormessageComponent implements OnInit {

  public showErrorMessage = true;
  title: string;
  message: string;
  constructor(
    private service: ErrorService,
    private cvRef: ChangeDetectorRef,
    public bsModalRef: BsModalRef,
  ) { }

  ngOnInit(): void {
    this.init();
  }

  init() {
    this.service.getErrorObserver()
      .subscribe((status) => {
        this.showErrorMessage = status == "start";
        this.cvRef.detectChanges();
      });
  }
}