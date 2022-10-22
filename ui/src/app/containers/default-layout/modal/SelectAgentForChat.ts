import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef, ModalOptions } from "ngx-bootstrap/modal";
import { Subscription } from "rxjs";
import { AppUtility } from "../../../config/AppUtility";
import { ChatMessage } from "../../../model/ChatMessage";
import { LoggedInUser } from "../../../model/LoggedInUser";
import { Permission } from "../../../model/Permssion";
import { ChatRoomService } from "../../../service/ChatRoomService";
import { ChatService } from "../../../service/ChatService";
import { ChatWSService } from "../../../service/ChatWSService";
import { LoggedInUserService } from "../../../service/LoggedInUserService";
import { MiscService } from "../../../service/MiscService";
declare var $: any;

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-male"></i> Chat with an agent instantly </h6>
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
            <h6>Please search with module, category and sub category!</h6>
          </div>
        </div>
        <form [ngClass]="{'d-none': canAssign === false}" [formGroup]="angFormAssign" (ngSubmit)="assignIncident()"
          novalidate>
          <div class="card-body">
            <div class="row">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="parentModule">Module</label>
                <ng-select [items]="parentModuleList" placeholder="-- Select Module --" #parentModule
                  (change)="populateModule($event)" formControlName="parentModule" id="parentModule" name="parentModule"
                  bindLabel="label" bindValue="id">
                </ng-select>
                <div *ngIf="f.parentModule.touched && f.parentModule.invalid" class="alert alert-danger-custom">
                  <div *ngIf="f.parentModule.errors?.required">
                    Parent Module is required.
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="moduleId">
                  <span *ngIf="f.parentModule.value=='ASSET'">Product Category</span>
                  <span *ngIf="f.parentModule.value=='INCIDENT'||f.parentModule.value==''||f.parentModule.value==null">
                    Category</span>
                </label>
                <ng-select [items]="moduleList" placeholder="-- Select Product Category --" #moduleId
                  (change)="populateSubmodule($event)" formControlName="moduleId" id="moduleId" name="moduleId"
                  bindLabel="customerModuleName" bindValue="moduleId">
                </ng-select>
                <div *ngIf="f.moduleId.touched && f.moduleId.invalid" class="alert alert-danger-custom">
                  <div *ngIf="f.moduleId.errors?.required">
                    Field is required.
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="subModuleId">
                  <span *ngIf="f.parentModule.value=='ASSET'">Product Sub Category</span>
                  <span
                    *ngIf="f.parentModule.value=='INCIDENT'||f.parentModule.value==''||f.parentModule.value==null">Sub
                    Category</span>
                </label>
                <ng-select [items]="subModuleList" placeholder="-- Select Asset Category --"
                  formControlName="subModuleId" id="subModuleId" name="subModuleId" bindLabel="customerModuleName"
                  bindValue="moduleId">
                </ng-select>
                <div *ngIf="f.subModuleId.touched && f.subModuleId.invalid" class="alert alert-danger-custom">
                  <div *ngIf="f.subModuleId.errors?.required">
                    Field is required.
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col">
                <label class="form-col-form-label required-control-label" for="subModuleId">
                  <span>Message</span>
                </label>
                <textarea id="message" rows="4" class="form-control textarea-non-resizable"
                  formControlName="message" id="message" name="message" placeholder="Message"></textarea>
                <div *ngIf="f.message.touched && f.message.invalid" class="alert alert-danger-custom">
                  <div *ngIf="f.message.errors?.required">
                    Message is required!
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <button [disabled]="!angFormAssign.valid" type="submit" class="btn btn-primary btn-sm">
              <i class="fa fa-male"></i> Start Chat
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
export class SelectAgentForChat implements OnInit {
  canAssign: boolean;
  angFormAssign: FormGroup;
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  parentModuleList: any[] = [];
  loggedInUser: LoggedInUser;
  subscription: Subscription;
  chatRoomId: string;;


  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    public options: ModalOptions,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private chatRoomService: ChatRoomService,
  ) {
    this.parentModuleList.push({ id: 'ASSET', label: 'Asset' });
    this.parentModuleList.push({ id: 'INCIDENT', label: 'Incident' });
    this.permissions = this.loggedInUserService.getModulePermissions();
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    // let p;
    // this.moduleList = [];
    // for (let i in this.permissions) {
    //     if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == "ASSET") {
    //         p = new Permission(this.permissions[i]);
    //         p.customerModuleName = AppUtility.toTitleCase(p.customerModuleName);
    //         this.moduleList.push(p);
    //     }
    // }
    this.angFormAssign = this.fb.group({
      parentModule: new FormControl(null, [
        Validators.required,
      ]),
      moduleId: new FormControl(null, [
        Validators.required,
      ]),
      subModuleId: new FormControl(null, [
        Validators.required,
      ]),
      message: new FormControl(null, [
        Validators.required,
      ]),
    });
  }
  ngOnInit() {
    this.subscription = this.chatRoomService.currentChatRoomId.subscribe(chatRoomId => this.chatRoomId = chatRoomId);
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  get f() { return this.angFormAssign.controls; }

  initiateChat(req: any) {
    this.miscService.startChatService(req)
      .subscribe((data: any) => {
        //console.log("completed....", JSON.stringify(data));
        this.bsModalRef.hide();
        this.chatRoomService.setChatRoomId(data.roomId);
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  assignIncident() {
    if (this.angFormAssign.valid) {
      var chatmessage = new ChatMessage();
      chatmessage.content = this.angFormAssign.controls['message'].value;
      chatmessage.moduleId = this.angFormAssign.controls['moduleId'].value;
      chatmessage.subModuleId = this.angFormAssign.controls['subModuleId'].value;
      chatmessage.attachments = [];
      chatmessage.recipientId = null;
      chatmessage.recipientName = null;
      chatmessage.senderId = this.loggedInUser.userId;
      chatmessage.senderName = null;
      chatmessage.status = null;
      chatmessage.intiateChat = true;
      this.initiateChat(chatmessage);
    } else {
      console.log("Invalid Form!");
    }
  }

  @ViewChild("parentModule") parentModule;
  populateModule(parentModule) {
    if (typeof parentModule !== 'undefined') {
      let c = 0;
      this.moduleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == parentModule.id) {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
          this.moduleList[c] = p;
          c++;
        }
      }
      this.angFormAssign.controls['moduleId'].setValue(null);
      this.angFormAssign.controls['subModuleId'].setValue(null);
    } else {
      this.angFormAssign.controls['moduleId'].setValue(null);
      this.angFormAssign.controls['subModuleId'].setValue(null);
    }
  }

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    if (typeof moduleId !== 'undefined') {
      let c = 0;
      this.subModuleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == moduleId.moduleId && this.permissions[i].licenseType == this.angFormAssign.controls['parentModule'].value) {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
      this.angFormAssign.controls['subModuleId'].setValue(null);
    } else {
      this.angFormAssign.controls['subModuleId'].setValue(null);
    }
  }

  @ViewChild("subModuleId") subModuleId;
  populateUser(subModuleId) {
    // let c = 0;
    // if (typeof subModuleId !== 'undefined') {
    //     let moduleId = this.moduleIds[0];
    //     this.moduleIds = [];
    //     this.moduleIds.push(moduleId);
    //     this.moduleIds.push(subModuleId.moduleId);
    //     let params = {
    //         "moduleIds": this.moduleIds.join(','),
    //         "actionName": 'SOLVE',
    //     };
    //     this.agentList = [];
    //     // this.userService
    //     //     .getUsersByModuleAndAction(params)
    //     //     .subscribe((data: UserVO[]) => {
    //     //         this.agentList = [];
    //     //         for (let i = 0; i < data.length; i++) {
    //     //             if (
    //     //                 this.loggedInUserService.hasRole('ORG_ASSET_AGENT_LEAD')
    //     //                 ||
    //     //                 this.loggedInUserService.hasRole('ORG_ASSET_AGENT_MANAGER')
    //     //             ) {
    //     //                 if (this.confirmAgentRole(data[i].roleNames))
    //     //                     this.agentList.push(data[i]);
    //     //             } else {
    //     //                 this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    //     //                 if (this.loggedInUser.userId == data[i].id)
    //     //                     this.agentList.push(data[i]);
    //     //             }
    //     //             //this.agentList.push(data[i]);
    //     //         }
    //     //         //console.log(data);
    //     //     });
    //     this.angFormAssign.controls['assignedUser'].setValue(null);
    // } else {
    //     this.angFormAssign.controls['assignedUser'].setValue(null);
    // }
  }
}