import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { Action } from "../../../../model/Action";

@Component({
    selector: 'modal-content',
    template: `<div class="modal-header">
    <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Permisable Actions</h6>
    <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
        <span aria-hidden="true">&times;</span>
    </button>
</div>
<div class="modal-body">
    <div class="row">
        <div class="col-sm-12">
            <div class="card ">
            <div class="card-body">
                <table class="table table-bordered">
                    <tr *ngFor="let item of permisableActions;">
                        <td>{{item.name}}</td>
                    </tr>
                </table>
                </div> </div>
        </div>
    </div>
</div>`
})
export class ViewPermissableAction implements OnInit {
    permisableActions: Action[];
    constructor(public bsModalRef: BsModalRef, public options: ModalOptions, commonModule: CommonModule) { }
    ngOnInit() { }
    ngAfterViewInit() { }
    ngAfterContentInit() { }

}