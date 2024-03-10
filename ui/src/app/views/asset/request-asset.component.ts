import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { Role } from '../../model/Role';
import { MiscService } from '../../service/MiscService';
import { Priority, PriorityList } from '../../model/Priority';
import { Incident, IncidentList } from '../../model/Incident';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { Permission } from '../../model/Permssion';
import { UserVO, UserVOListResponse } from '../../model/UserVO';
import { IncidentCommunication } from '../../model/IncidentCommunication';
import { IncidentService } from '../../service/IncidentService';
import { MediaService } from '../../service/MediaService';
import { EntityType } from '../../model/enumerator/EntityType';
import { MediaType } from '../../model/enumerator/MediaType';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { Status } from '../../model/enumerator/Status';
import { ClientStorageService } from '../../service/ClientStorageService';
import { AssetService } from '../../service/AssetService';
import { Asset, AssetList } from '../../model/Asset';
declare var $: any;

@Component({
  selector: 'app-request-asset',
  templateUrl: './request-asset.component.html',
  styleUrls: ['./request-asset.component.scss']
})
export class RequestAssetComponent implements OnInit {
  moduleName: string = "MY ASSET,REQUESTED ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  newEntity: boolean = true;
  defaultStatus: any = 'OPEN';
  statusFlag: boolean = true;
  entityId: number;
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  priorities: any[] = [];
  roles: Role[] = [];
  users: UserVO[] = [];
  availableAssetList: any[] = [];
  allocatedAssetList: any[] = [];
  ticketTypes: any[] = [];
  incident: Incident;
  incidentCommunication: IncidentCommunication;
  incidentCommunications: IncidentCommunication[] = [];
  statusList: any = [];
  selectedFiles?: FileList;
  allSelectedFiles?: File[] = [];
  angForm: FormGroup;
  ckeditorToolbarConfig: any;
  readOnlyckeditorToolbarConfig: any;
  hasAgentPermission: boolean;
  mngrDtl: UserVO;
  draftData: any = { "new": null, "existing": [] };
  referer: string;
  mode: string;
  canEdit: boolean = true;
  asstes: any[] = [];
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private userService: UserService,
    private incidentService: IncidentService,
    private mediaService: MediaService,
    private clientStorageService: ClientStorageService,
    private assetService: AssetService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.incident = new Incident();
    this.incident.status = this.defaultStatus;
    this.hasAgentPermission = false;
    this.mngrDtl = new UserVO();
    this.route.params.subscribe(params => {
      this.referer = this.route.snapshot.paramMap.get('referer');
      this.mode = this.route.snapshot.paramMap.get('mode');
    });
    for (let item in IncidentStatus) {
      if (item != "ALL" && item != "OPEN"
        && item != "ASSIGNED" && item != "SLA_ABOUT_TO_BREACH"
        && item != "SLA_BREACHED" && item != "CLARIFICATION_PROVIDED") {
        this.statusList.push({ "key": item, "value": IncidentStatus[item] });
      }
    }
    this.statusList.sort(function (a, b) {
      if (b.key > a.key) return -1;
      if (a.key > b.key) return 1;
      return 0;
    });

    this.ticketTypes.push({ id: "ALLOCATE", label: "Allocation" });
    this.ticketTypes.push({ id: "DEALLOCATE", label: "Deallocation" });

    //this.assignedAssetList.push({ id: "DEALLOCATE", labelView: "Deallocate" });

    this.permissions = this.loggedInUserService.getModulePermissions();
    let p;
    this.moduleList = [];
    this.allocatedAssetList = [];
    this.availableAssetList = [];
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == 'ASSET') {
        p = new Permission(this.permissions[i]);
        p.customerModuleName = p.customerModuleName;
        //AppUtility.toTitleCase(p.customerModuleName);
        this.moduleList.push(p);
      }
    }
    if (!this.route.snapshot.paramMap.has('id')) {
      this.angForm = this.fb.group({
        moduleId: new FormControl(null, [
          Validators.required,
        ]),
        subModuleId: new FormControl(null, [
          Validators.required,
        ]),
        ticketType: new FormControl(null, [
          Validators.required,
        ]),
        deallocateId: new FormControl(null, [
        ]),
        priorityId: new FormControl(null, [
          Validators.required,
        ]),
        title: new FormControl('', [
          Validators.required,
          Validators.maxLength(255),
        ]),
        watchList: new FormControl(null, [
        ]),
        fileInput: new FormControl('', [
        ]),
        message: new FormControl('', [
          Validators.required,
        ]),
      }, {
        validators: this.newCustomValidations(),
      });
      this.miscService.prioritiesService({ "priorityType": "ASSET", "sort": "name,asc" })
        .subscribe((result: PriorityList) => {
          this.priorities = result.content;
          for (let k in this.priorities) {
            this.priorities[k].label = this.priorities[k].name + " (" + this.priorities[k].description + ") ";
          }
          this.userService.getUsersService()
            .subscribe((result: UserVOListResponse) => {
              let logedinUser = this.loggedInUserService.getLoggedInUser();
              this.users = [];
              for (let i = 0; i < result.content.length; i++) {
                if (logedinUser.userId != result.content[i].id)
                  this.users.push(result.content[i]);
              }
              let c = 0;
              this.incidentService
                .userAllocatedAssetsService({ incidentType: "ASSET", assigned: 1 })
                .subscribe((result: IncidentList) => {
                  let data = result.content;
                  for (let k in data) {
                    if (data[k].asset != null) {
                      //console.log(data[k].asset);
                      this.availableAssetList[c] = data[k].asset;
                      this.availableAssetList[c].txt = data[k].asset.modelNo + "/" + data[k].asset.serialNo;
                      c++;
                    }
                  }
                  this.allocatedAssetList = this.availableAssetList;
                  if (this.mode === 'draft' && this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY)) {
                    this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY));
                    if (this.draftData.new != null) {
                      this.populateSubmodule({ moduleId: this.draftData.new.moduleId });
                      this.angForm.get('moduleId').setValue(this.draftData.new.moduleId);
                      this.angForm.get('subModuleId').setValue(this.draftData.new.subModuleId);
                      this.angForm.get('priorityId').setValue(this.draftData.new.priority.id);
                      this.angForm.get('ticketType').setValue(this.draftData.new.ticketType);
                      this.populateAsset({ id: this.draftData.new.ticketType });
                      this.angForm.get('deallocateId').setValue(this.draftData.new.oldAssetId);
                      if (this.draftData.new.watchList != null) {
                        this.angForm.get('watchList').setValue(this.draftData.new.watchList.map(String));
                      }
                      this.angForm.get('title').setValue(this.draftData.new.title);
                      this.angForm.get('message').setValue(this.draftData.new.communications[0].message);
                    }
                  }
                });
            });
        });
    } else {
      this.angForm = this.fb.group({
        priorityId: new FormControl(null, [
          Validators.required,
        ]),
        newStatus: new FormControl(null, [
        ]),
        fileInput: new FormControl('', [
        ]),
        asset: new FormControl(null, [
        ]),
        assetValidity: new FormControl('', [
        ]),
        message: new FormControl('', [
          Validators.required,
        ]),
      }, {
        validators: this.customValidations(),
      });
      this.miscService.prioritiesService({ "priorityType": "ASSET", "sort": "name,asc" })
        .subscribe((result: PriorityList) => {
          this.priorities = result.content;
          for (let k in this.priorities) {
            this.priorities[k].label = this.priorities[k].name + " (" + this.priorities[k].description + ") ";
          }
          this.userService.getUsersService()
            .subscribe((result: UserVOListResponse) => {
              this.users = result.content;
            });
        });
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.callIncidentService(this.entityId);
    }
  }

  customValidations() {
    return (formGroup: FormGroup) => {
      // if (formGroup.controls['newStatus'].value == null) {
      //   formGroup.controls['existingAgreement'].setErrors({ required: true, notValidAgreement: false });
      // } else {
      //   if (formGroup.controls['existingAgreement'].value == '0') {
      //     formGroup.controls['existingAgreement'].setErrors({ required: false, notValidAgreement: true });
      //   } else {
      //     formGroup.controls['existingAgreement'].setErrors(null);
      //   }
      // }
    };
  }

  newCustomValidations() {
    return (formGroup: FormGroup) => {
      if (formGroup.controls['ticketType'].value == "DEALLOCATE") {
        if (formGroup.controls['deallocateId'].value == null) {
          formGroup.controls['deallocateId'].setErrors({ required: true, });
        } else {
          formGroup.controls['deallocateId'].setErrors(null);
        }
      } else {
        formGroup.controls['deallocateId'].setErrors(null);
      }
    };
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

  ngOnInit(): void { }

  ngAfterViewInit() { }

  ngAfterContentInit() {
    $(function () {
      $("#comments").accordion({
        //icons: { "header": "ui-icon-plus", "activeHeader": "ui-icon-minus" },
        heightStyle: "content",
        active: false,
        collapsible: true,
        activate: function (event, ui) {
          var index = $(this).accordion("option", "active");
          console.log(index);
          if (index === false) {
            $('#comments-action').addClass('fa-plus-circle');
            $('#comments-action').removeClass('fa-minus-circle');
            $('#highlight').text('Expand');
          }
          else {
            $('#comments-action').removeClass('fa-plus-circle');
            $('#comments-action').addClass('fa-minus-circle');
            $('#highlight').text('Collapse');
          }
        }
      });
    });
  }

  get f() { return this.angForm.controls; }

  formSubmit(sts: any) {
    if (this.angForm.valid) {
      if (sts === "DRAFT") {
        let returnPath = '/asset/requested/outgoing';
        if (this.referer === 'agent-all') {
          returnPath = '/asset/requested/incomming';
        } else if (this.referer === 'agent-mine') {
          returnPath = '/incident/agent/mine';
        }
        let inc = new Incident();
        let ic = new IncidentCommunication();
        let priorityId = this.angForm.controls['priorityId'].value;
        let priority = new Priority();
        for (let i in this.priorities) {
          if (this.priorities[i].id == priorityId) {
            priority.id = priorityId;
            priority.name = this.priorities[i].name;
            priority.version = this.priorities[i].version;
          }
        }
        let asset = this.incident.asset != null ? { id: this.incident.asset.id, modelNo: this.incident.asset.modelNo, serialNo: this.incident.asset.serialNo, } : null;
        for (let i in this.asstes) {
          if (this.asstes[i].id == this.angForm.controls['asset'].value) {
            asset = { id: this.asstes[i].id, modelNo: this.asstes[i].modelNo, serialNo: this.asstes[i].serialNo, };
          }
        }
        if (this.newEntity) {
          inc.ticketType = this.angForm.controls['ticketType'].value;
          if (this.angForm.controls['ticketType'].value == "DEALLOCATE")
            inc.oldAssetId = this.angForm.controls['deallocateId'].value;
          inc.moduleId = this.angForm.controls['moduleId'].value;
          inc.subModuleId = this.angForm.controls['subModuleId'].value;
          inc.title = this.angForm.controls['title'].value;
          inc.watchList = this.angForm.controls['watchList'].value;
          inc.priority = priority;
          inc.status = sts;
          ic = new IncidentCommunication();
          ic.message = this.angForm.controls['message'].value;
          inc.communications.push(ic);
          this.draftData.new = inc;
          this.clientStorageService.set(AppUtility.APP_ASSET_DRAFT_KEY, JSON.stringify(this.draftData));
          this.router.navigate([returnPath]);
        } else {
          ic = new IncidentCommunication();
          ic.message = this.angForm.controls['message'].value;
          if (this.hasAgentPermission && this.canEdit) {
            inc.priority = priority;
            inc.asset = asset;
            inc.status = this.angForm.controls['newStatus'].value;
            if (this.angForm.controls['assetValidity'].value != "") {
              let mnt = moment(this.angForm.controls['assetValidity'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT);
              inc.assetValidity = moment.utc(mnt).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T23:59:59";
            } else {
              inc.assetValidity = moment().utc().format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT) + "T23:59:59";
            }
          }
          inc.communications.push(ic);
          inc.id = this.incident.id;
          if (this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY)) {
            this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY));
            let hasDraftData = false;
            for (let k in this.draftData.existing) {
              if (this.draftData.existing[k].id == this.incident.id) {
                this.draftData.existing[k] = inc;
                hasDraftData = true;
                break;
              } else {
                continue;
              }
            }
            if (!hasDraftData) {
              this.draftData.existing.push(inc);
            }
          } else {
            this.draftData.existing.push(inc);
          }
          this.clientStorageService.set(AppUtility.APP_ASSET_DRAFT_KEY, JSON.stringify(this.draftData));
          this.router.navigate([returnPath]);
        }
      } else {
        let priorityId = this.angForm.controls['priorityId'].value;
        let priority = new Priority();
        for (let i in this.priorities) {
          if (this.priorities[i].id == priorityId) {
            priority.id = priorityId;
            priority.name = this.priorities[i].name;
            priority.version = this.priorities[i].version;
            priority.organisation = null;
          }
        }
        let asset = this.incident.asset != null ? { id: this.incident.asset.id, modelNo: this.incident.asset.modelNo, version: this.incident.asset.version, serialNo: this.incident.asset.serialNo, } : null;
        for (let i in this.asstes) {
          if (this.asstes[i].id == this.angForm.controls['asset'].value) {
            asset = { id: this.asstes[i].id, modelNo: this.asstes[i].modelNo, serialNo: this.asstes[i].serialNo, version: this.asstes[i].version, };
          }
        }
        //console.log(asset);
        if (this.newEntity) {
          this.incident.ticketType = this.angForm.controls['ticketType'].value;
          if (this.angForm.controls['ticketType'].value == "DEALLOCATE") {
            this.incident.oldAssetId = this.angForm.controls['deallocateId'].value;
          }
          for (let i in this.availableAssetList) {
            if (this.incident.oldAssetId == this.availableAssetList[i].id) {
              this.incident.oldAsset = this.availableAssetList[i].modelNo + "/" + this.availableAssetList[i].serialNo;
            }
          }
          this.incident.moduleId = this.angForm.controls['moduleId'].value;
          this.incident.subModuleId = this.angForm.controls['subModuleId'].value;
          this.incident.title = this.angForm.controls['title'].value;
          this.incident.watchList = this.angForm.controls['watchList'].value;
          this.incident.assignedUser = null;
          this.incident.raisedUser = null;
          this.incident.priority = priority;
          this.incident.status = this.defaultStatus;
          for (let i = 0; i < this.permissions.length; i++) {
            if (this.permissions[i].moduleId == this.angForm.controls['subModuleId'].value && this.permissions[i].licenseType == 'ASSET') {
              this.incident.assetApproved = !this.permissions[i].requireApproval;
              this.incident.feedbackProvided = !this.permissions[i].requireApproval;
            }
          }
        } else {
          if (this.hasAgentPermission && this.canEdit) {
            this.incident.priority = priority;
            this.incident.asset = asset;
            if (asset != null)
              this.incident.oldAsset = asset.modelNo + '/' + asset.serialNo;
            if (this.angForm.controls['newStatus'].value != null && this.angForm.controls['newStatus'].value != '') {
              this.incident.status = this.angForm.controls['newStatus'].value;
            }
          } else {
            this.incident.priority = priority;
          }
          this.incident.assetValidity = AppUtility.prepareDateToDateTimeString(moment(this.angForm.controls['assetValidity'].value, AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate());
        }
        // prepare incidentCommunication object
        this.incidentCommunication = new IncidentCommunication();
        this.incidentCommunication.message = this.angForm.controls['message'].value;
        this.incidentCommunication.communicatedBy = null;
        this.incidentCommunication.incident = null;
        this.incident.communications.push(this.incidentCommunication);
        this.incident.organisation = { id: this.loggedInUserService.getLoggedInUser().organisationId };
        //console.log(this.incident);
        this.incident.incidentType = 2;
        //console.log(this.incident);
        this.callSaveIncidentService();
      }
    } else {
      console.log("Invalid Form!");
    }
  }

  getFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angForm.controls['fileInput'];
      if (file.errors && !file.errors.validAttachments && !file.errors.mustBeLessThan2MB) {
        return;
      }
      let validMimeTpes = ["application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "application/x-msexcel", "application/x-excel", "application/vnd.ms-excel", "application/excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingm", "image/jpeg", "image/pjpeg", "image/png"];
      if (!validMimeTpes.includes(type)) {
        file.setErrors({ validAttachments: true, mustBeLessThan2MB: false });
      } else if (size > (3145728)) {
        file.setErrors({ validAttachments: false, mustBeLessThan2MB: true });
      } else {
        file.setErrors(null);
        this.selectedFiles = event.target.files;
        for (var i = 0; i < this.selectedFiles.length; i++) {
          this.allSelectedFiles.push(this.selectedFiles[i]);
        }
        //console.log(this.allSelectedFiles);
      }
      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");
    }
  }

  handleDraftData() {
    this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY));
    if (this.draftData != null) {
      if (this.newEntity && this.mode == "draft") {
        this.draftData.new = null;
      } else {
        let existingData = [];
        for (let k in this.draftData.existing) {
          if (this.draftData.existing[k].id != this.entityId) {
            existingData.push(this.draftData.existing[k]);
          }
        }
        this.draftData.existing = existingData;
      }
      this.clientStorageService.set(AppUtility.APP_ASSET_DRAFT_KEY, JSON.stringify(this.draftData));
    }
  }

  goBack() {
    this.handleDraftData();
    this._location.back();
  }

  removeFile(indx) {
    if (indx > -1) { // only splice array when item is found
      this.allSelectedFiles.splice(indx, 1); // 2nd parameter means remove one item only
      $(function () {
        $('#attached-file-' + indx).remove();
      });
    }
  }

  callSaveIncidentService() {
    let returnPath = '/asset/requested/outgoing';
    if (this.referer === 'agent-all') {
      returnPath = '/asset/requested/incomming';
    } else if (this.referer === 'agent-mine') {
      returnPath = '/incident/agent/mine';
    }
    this.incidentService
      .saveIncidentService(this.incident)
      .subscribe((data: Incident) => {
        //console.log(data);
        this.handleDraftData();
        if (typeof this.allSelectedFiles != "undefined") {
          if (this.allSelectedFiles.length > 0) {
            const formData: FormData = new FormData();
            for (var i = 0; i < this.allSelectedFiles.length; i++) {
              formData.append("file", this.allSelectedFiles[i]);
            }
            let headers = new Headers();
            headers.append('Content-Type', 'multipart/form-data');
            headers.set('Accept', 'application/json');
            let commId = data.communications[0].id;
            this.mediaService
              .saveMediaService(commId, EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION, "NA", formData, { 'headers': headers })
              .subscribe((data: any) => {
                this.router.navigate([returnPath]);
              });
          } else {
            this.router.navigate([returnPath]);
          }
        } else {
          this.router.navigate([returnPath]);
        }
        // if (typeof this.selectedFiles != "undefined") {
        //   if (this.selectedFiles.length > 0) {
        //     const formData: FormData = new FormData();
        //     for (var i = 0; i < this.selectedFiles.length; i++) {
        //       formData.append("file", this.selectedFiles[i]);
        //     }
        //     let headers = new Headers();
        //     headers.append('Content-Type', 'multipart/form-data');
        //     headers.set('Accept', 'application/json');
        //     let commId = data.communications[0].id;
        //     this.mediaService
        //       .saveMediaService(commId, EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION, "NA", formData, { 'headers': headers })
        //       .subscribe((data: any) => {
        //         this.router.navigate([returnPath]);
        //       });
        //   } else {
        //     this.router.navigate([returnPath]);
        //   }
        // } else {
        //   this.router.navigate([returnPath]);
        // }
      });
  }

  callIncidentService(id: number) {
    this.incidentService
      .incidentService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.incident.assetValidity = data.assetValidity;
        this.incident.oldAsset = data.oldAsset;
        this.incident.oldAssetId = data.oldAssetId;
        this.incident.ticketType = data.ticketType;
        this.incident.version = data.version;
        this.incident.id = data.id;
        this.incident.moduleId = data.moduleId;
        this.incident.subModuleId = data.subModuleId;
        this.incident.priority = data.priority;
        this.incident.title = data.title;
        this.incident.watchList = data.watchList;
        this.incident.assignedUser = data.assignedUser;
        this.incident.raisedUser = data.raisedUser;
        this.incident.slaAt = data.slaAt;
        this.incident.incidentNo = data.incidentNo;
        this.incident.raisedAt = data.raisedAt;
        this.incident.status = data.status;
        this.incident.holdAt = data.holdAt;
        this.incident.moduleName = data.moduleName;
        this.incident.subModuleName = data.subModuleName;
        this.incident.communications = data.communications;
        this.incidentCommunications = data.communications;
        this.incident.asset = data.asset != null ? { modelNo: data.asset.modelNo, serialNo: data.asset.serialNo, id: data.asset.id, version: data.asset.version } : null;
        this.incident.assetApproved = data.assetApproved;
        this.incident.feedbackProvided = data.feedbackProvided;
        this.incident.allocated = data.allocated;
        this.incident.oldAssetId = data.oldAssetId;
        this.incident.deAllocated = data.deAllocated;
        let org = data.organisation;
        this.incident.organisation = { id: org.id, version: org.version };
        for (let k in this.incident.communications) {
          this.incident.communications[k].communicatedBy.status = Status[this.incident.communications[k].communicatedBy.status];
        }
        if (this.incident.assignedUser != null) {
          this.incident.assignedUser.status = Status[this.incident.assignedUser.status];
        }
        if (this.incident.raisedUser != null) {
          this.incident.raisedUser.status = Status[this.incident.raisedUser.status];
        }
        let actualModuleName = data.actualModuleName;
        let actualSubModuleName = data.actualSubModuleName;
        let categoryAdminRoleName = "ORG_" + actualModuleName + "_CATEGORY_ADMIN";
        let logedinUser = this.loggedInUserService.getLoggedInUser();
        this.hasAgentPermission = false;
        if (this.incident.raisedUser.id == logedinUser.userId) {
          //console.log("raised user who raised the incident");
          // for user who raised the incident
          this.canEdit = true;
        } else if (this.incident.assignedUser != null && this.incident.assignedUser.id == logedinUser.userId) {
          // for assigned agent who can edit, after assignment
          this.getAssets();
          this.canEdit = true;
          this.hasAgentPermission = true;
          //this.populateSubmodule({ moduleId: this.incident.moduleId });
        } else if (this.incident.assignedUser != null && this.loggedInUserService.hasPermissionById(this.incident.moduleId, 'SOLVE') && this.loggedInUserService.hasPermissionById(this.incident.subModuleId, 'SOLVE')) {
          // for agent lead/manager who can edit, after assignment
          //console.log("agent lead/manager who can edit, after assignment");
          this.canEdit = true;
          this.hasAgentPermission = true;
          this.getAssets();
        } else if (this.loggedInUserService.hasPermissionById(this.incident.moduleId, 'SOLVE') && this.loggedInUserService.hasPermissionById(this.incident.subModuleId, 'SOLVE')) {
          // for agent, agent lead/manager who can only view because incident not assigned yet to any one.
          //console.log("agent, agent lead/manager who can only view because incident not assigned yet to any one.");
          this.canEdit = false;
          this.hasAgentPermission = true;
          //this.populateSubmodule({ moduleId: this.incident.moduleId });
        } else if (this.loggedInUserService.hasRole("ORG_ADMIN")) {
          // for org admin user only can view incident details
          //console.log("org admin user only can view incident details");
          this.canEdit = false;
        } else if (this.loggedInUserService.hasRole(categoryAdminRoleName)) {
          // for category admin user only can view incident details
          //console.log("category admin user only can view incident details");
          this.canEdit = false;
        } else {
          //console.log("user don't have any access to this incident");
          // for user don't have any access to this incident
          this.router.navigate(['/no-access']);
        }
        this.angForm.get('priorityId').setValue(this.incident.priority.id);
        this.angForm.get('assetValidity').setValue(moment(this.incident.assetValidity).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
        //console.log(JSON.stringify(this.incident));
        /*this.angForm.get('status').setValue(this.incident.status);
        console.log(JSON.stringify(this.incident));
        this.angForm.markAllAsTouched();*/
        //getagent manager details
        this.userService
          .getUserService(this.incident.raisedUser.managerId)
          .subscribe((data: any) => {
            this.mngrDtl = data;
          });
        //check if has draft data. if so the populate fields
        if (this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY)) {
          this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_ASSET_DRAFT_KEY));
          if (this.draftData.existing.length > 0) {
            for (let k in this.draftData.existing) {
              if (this.draftData.existing[k].id == this.entityId) {
                if (this.hasAgentPermission) {
                  this.angForm.get('priorityId').setValue(this.draftData.existing[k].priority.id);
                  this.angForm.get('newStatus').setValue(this.draftData.existing[k].status);
                  if (this.draftData.existing[k].asset != null) {
                    this.angForm.get('asset').setValue(this.draftData.existing[k].asset.id);
                  }
                  this.angForm.get('assetValidity').setValue(moment(this.draftData.existing[k].assetValidity).format(AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT));
                }
                this.angForm.get('message').setValue(this.draftData.existing[k].communications[0].message);
                break;
              } else {
                continue;
              }
            }
          }
        }
      });
  }

  getAssets() {
    this.assetService
      .availableAssetsService({ available: 1, productCategory: this.incident.moduleId, assetType: this.incident.subModuleId, requestRaisedId: this.incident.raisedUser.id, })
      .subscribe((data: AssetList) => {
        this.asstes = [];
        this.asstes = data.content;
        for (let i in this.asstes) {
          this.asstes[i].label = this.asstes[i].modelNo + "/" + this.asstes[i].serialNo;
        }
      });
  }

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    if (typeof moduleId !== 'undefined') {
      //console.log(JSON.stringify(this.permissions));
      let c = 0;
      this.subModuleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].moduleParentId == moduleId.moduleId && this.permissions[i].licenseType == 'ASSET') {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
      this.angForm.controls['subModuleId'].setValue(null);
    } else {
      this.angForm.controls['subModuleId'].setValue(null);
    }
  }

  @ViewChild("ticketType") ticketType;
  populateAsset(ticketType) {
    if (typeof ticketType !== 'undefined') {
      if (ticketType.id == "DEALLOCATE") {
        let c = 0;
        this.allocatedAssetList = [];
        for (let i in this.availableAssetList) {
          if (
            this.availableAssetList[i].moduleId == this.angForm.controls['moduleId'].value
            && this.availableAssetList[i].subModuleId == this.angForm.controls['subModuleId'].value
          ) {
            this.allocatedAssetList[c] = this.availableAssetList[i];
            this.allocatedAssetList[c].txt = this.availableAssetList[i].modelNo + "/" + this.availableAssetList[i].serialNo;
            c++;
          }
        }
        $('.asset-dealloc-row').removeClass('d-none');
        this.angForm.controls['deallocateId'].setValue(null);
      } else {
        this.allocatedAssetList = [];
        $('.asset-dealloc-row').addClass('d-none');
        this.angForm.controls['deallocateId'].setValue(null);
      }
    } else {
      $('.asset-dealloc-row').addClass('d-none');
      this.angForm.controls['deallocateId'].setValue(null);
    }
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

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  makeTitleCase(str: string): string {
    if (str != '' && typeof str != "undefined") {
      //return AppUtility.toTitleCase(str);
      return (str);
    }
    return '';
  }

  resetFile(element) {
    element.value = "";
  }

}