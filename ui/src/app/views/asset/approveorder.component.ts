import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { AssetOrder } from '../../model/AssetOrder';
import { AssetOrderService } from '../../service/AssetOrderService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { AssetOrderApprovalDTO } from '../../model/AssetOrderApprovalDTO';
declare var $: any;

@Component({
  selector: 'app-approveorder',
  templateUrl: './approveorder.component.html',
  styleUrls: ['./approveorder.component.scss']
})
export class ApproveOrderComponent implements OnInit {
  moduleName: string = "ORDER ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'OPEN';
  statusFlag: boolean = true;
  entityId: number;
  angForm: FormGroup;
  hasAgentPermission: boolean;
  assetOrder: AssetOrder;
  approver: number;
  canApprove1: boolean = false;
  canApprove2: boolean = false;
  loggedInUser: LoggedInUser;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
    private assetOrderService: AssetOrderService,
  ) {
    if (!this.hasPermission("APPROVE")) {
      this.goBack();
    }
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.assetOrder = new AssetOrder();
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
  }

  hasPermission(actions: string): boolean {
    let modules = this.moduleName.split(",");
    let actionList = actions.split(",");
    for (let i in modules) {
      if (this.loggedInUserService.hasPermissionByName(modules[i], actionList[i])) {
        return true;
      }
    }
    return false;
  }

  getTitle(state, parent) {
    var data = [];
    if (parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }
    if (state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.approver = Number(this.route.snapshot.paramMap.get('approver'));
      //console.log(this.approver);
      if (this.approver != 1 && this.approver != 2) {
        this.goBack();
      }
      if (this.approver == 1) {
        this.angForm = this.fb.group({
          approverUser1Comment: new FormControl('', [
            Validators.required,
            Validators.maxLength(255),
          ]),
        });
      } else if (this.approver == 2) {
        this.angForm = this.fb.group({
          approverUser2Comment: new FormControl('', [
            Validators.required,
            Validators.maxLength(255),
          ]),
        });
      }
    });
    if (!this.route.snapshot.paramMap.has('id')) {
      this.goBack();
    } else {
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.callAssetOrderService(this.entityId);
    }
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  get f() { return this.angForm.controls; }

  formSubmit(mode: string) {
    let assetOrderApprovalDTO = new AssetOrderApprovalDTO();
    assetOrderApprovalDTO.id = this.assetOrder.id;
    let approved = mode == 'APPROVE' ? true : false;
    if (this.angForm.valid) {
      if (this.canApprove1) {
        if ($('#approverUser1Comment').val() == "") {
          $('#feedback1-err').removeClass('d-none');
          return false;
        } else {
          $('#feedback1-err').addClass('d-none');
          assetOrderApprovalDTO.approverNo = 1;
          assetOrderApprovalDTO.approval = approved;
          assetOrderApprovalDTO.feedback = this.angForm.controls['approverUser1Comment'].value;
        }
      } else if (this.canApprove2) {
        if ($('#approverUser2Comment').val() == "") {
          $('#feedback2-err').removeClass('d-none');
          return false;
        } else {
          $('#feedback2-err').addClass('d-none');
          assetOrderApprovalDTO.approverNo = 2;
          assetOrderApprovalDTO.approval = approved;
          assetOrderApprovalDTO.feedback = this.angForm.controls['approverUser2Comment'].value;
        }
      }
      console.log(JSON.stringify(assetOrderApprovalDTO));
      this.callApproveAssetOrder(assetOrderApprovalDTO);
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this._location.back();
  }

  callApproveAssetOrder(assetOrderApprovalDTO: AssetOrderApprovalDTO) {
    this.assetOrderService
      .approveAssetOrder(assetOrderApprovalDTO)
      .subscribe((data: any) => {
        this.router.navigate(['/dashboard']);
      });
  }

  callAssetOrderService(id: number) {
    this.assetOrderService
      .assetOrderService(id)
      .subscribe((data: any) => {
        this.assetOrder = data;
        if (this.approver == 1 && this.assetOrder.approverUser1.id == this.loggedInUser.userId) {
          if (this.assetOrder.approverUser1Comment != null) {
            this.canApprove1 = false;
          } else {
            this.canApprove1 = true;
          }
        } else if (this.approver == 2 && this.assetOrder.approverUser2.id == this.loggedInUser.userId) {
          if (this.assetOrder.approverUser2Comment != null) {
            this.canApprove2 = false;
          } else {
            this.canApprove2 = true;
          }
        } else {
          this.goBack();
        }
      });
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}