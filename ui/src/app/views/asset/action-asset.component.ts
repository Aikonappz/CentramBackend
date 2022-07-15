import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { AssetApprovalDTO } from '../../model/AssetApprovalDTO';
import { MediaService } from '../../service/MediaService';
import { Incident } from '../../model/Incident';
import { IncidentService } from '../../service/IncidentService';
declare var $: any;

@Component({
  selector: 'app-action-asset',
  templateUrl: './action-asset.component.html',
  styleUrls: ['./action-asset.component.scss']
})
export class AssetRequestActionComponent implements OnInit {
  moduleName: string = "MY ASSET";
  newEntity: boolean = true;
  entityId: number;
  angForm: FormGroup;
  requestId: number;
  canApprove: boolean = false;
  loggedInUser: LoggedInUser;
  incident: Incident;
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private mediaService: MediaService,
    private loggedInUserService: LoggedInUserService,
    private incidentService: IncidentService,
  ) {
    this.angForm = this.fb.group({
      approverComment: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
      ]),
    });
    this.incident = new Incident();
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.route.params.subscribe(params => {
      this.requestId = Number(this.route.snapshot.paramMap.get('requestId'));
      //console.log(this.approver);
      if (isNaN(this.requestId)) {
        this.router.navigate(['/dashboard']);
      }
      this.newEntity = false;
      this.entityId = this.requestId;
      this.callAssetRequestService(this.entityId);
    });
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
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
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
    $(function () {
    });
  }

  get f() { return this.angForm.controls; }

  formSubmit(mode: string) {
    let assetOrderApprovalDTO = new AssetApprovalDTO();
    assetOrderApprovalDTO.id = this.incident.id;
    let approved = mode == 'APPROVE' ? true : false;
    if (this.angForm.valid) {
      assetOrderApprovalDTO.approval = approved;
      assetOrderApprovalDTO.feedback = this.angForm.controls['approverComment'].value;
      console.log(JSON.stringify(assetOrderApprovalDTO));
      this.callApproveAssetRequest(assetOrderApprovalDTO);
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() {
    this._location.back();
  }

  callApproveAssetRequest(assetOrderApprovalDTO: AssetApprovalDTO) {
    this.incidentService
      .approveAssetRequest(assetOrderApprovalDTO)
      .subscribe((data: any) => {
        this.router.navigate(['/asset/approval/pending']);
      });
  }

  callAssetRequestService(id: number) {
    this.incidentService
      .incidentService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.incident = data;
        if (this.loggedInUser.userId != this.incident.raisedUser.managerId) {
          //console.log("no right to visit!");
          this.router.navigate(['/asset/approval/pending']);
        } else if (this.loggedInUser.userId == this.incident.raisedUser.managerId && this.incident.feedbackProvided) {
          //console.log("already feedback provided!");
          this.canApprove = false;
          //this.angForm = this.fb.group({});
        } else if (this.loggedInUser.userId == this.incident.raisedUser.managerId && !this.incident.feedbackProvided) {
          //console.log("can provide feedback!");
          this.canApprove = true;
        } else {
          //console.log("can't provide feedback!");
          this.router.navigate(['/asset/approval/pending']);
        }
      });
  }

  downloadFile(idFile: number) {
    this.mediaService
      .downloadMediaService(idFile, {})
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: data.type });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          //alert('Please disable your Pop-up blocker and try again.');
        }
      });
    return false;
  }
}