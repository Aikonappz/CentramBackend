import { Component, OnInit, ViewChild, } from '@angular/core';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { Role } from '../../model/Role';
import { MiscService } from '../../service/MiscService';
import { Priority, PriorityList } from '../../model/Priority';
import { Incident } from '../../model/Incident';
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
import { environment } from '../../../environments/environment';
import { TimeEntry } from '../../model/TimeEntry';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { AddTimeEntryComponent } from './modal/AddTimeEntryComponent';
import { ViewTimeEntryComponent } from './modal/ViewTimeEntryComponent';
declare var $: any;

@Component({
  selector: 'app-editincident',
  templateUrl: './editincident.component.html',
  styleUrls: ['./editincident.component.scss']
})
export class EditIncidentComponent implements OnInit {
  moduleName: string = "MY INCIDENTS,MY GROUP INCIDENTS";
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
  incident: Incident;
  incidentCommunication: IncidentCommunication;
  incidentCommunications: IncidentCommunication[] = [];
  statusList: any = [];
  selectedFiles?: FileList;
  allSelectedFiles?: File[] = [];
  allSelectedSolutionDocumentFiles?: File[] = [];
  angForm: FormGroup;
  ckeditorToolbarConfig: any;
  readOnlyckeditorToolbarConfig: any;
  hasAgentPermission: boolean;
  mngrDtl: UserVO;
  draftData: any = { "new": null, "existing": [] };
  referer: string;
  mode: string;
  canEdit: boolean = true;
  timeList: any[] = [];
  solutionDocumentEnabled: boolean = false;
  modalRef: BsModalRef;
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
    private modalService: BsModalService,
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

    let tmList = AppUtility.getSlaList(79);
    for (let k = 0; k < tmList.length; k++) {
      if (k != 0) {
        this.timeList.push({ id: tmList[k], label: tmList[k] + " hrs" });
      }
    }

    this.permissions = this.loggedInUserService.getModulePermissions();
    let p;
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == 'INCIDENT') {
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
        solutionDocument: new FormControl('', [
        ]),
        message: new FormControl('', [
          Validators.required,
        ]),
      });
      this.miscService.prioritiesService({ "priorityType": "INCIDENT", "sort": "name,asc" })
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
              if (this.mode === 'draft' && this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY)) {
                this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
                if (this.draftData.new != null) {
                  this.populateSubmodule({ moduleId: this.draftData.new.moduleId });
                  this.angForm.get('moduleId').setValue(this.draftData.new.moduleId);
                  this.angForm.get('subModuleId').setValue(this.draftData.new.subModuleId);
                  this.angForm.get('priorityId').setValue(this.draftData.new.priority.id);
                  if (this.draftData.new.watchList != null)
                    this.angForm.get('watchList').setValue(this.draftData.new.watchList.map(String));
                  this.angForm.get('title').setValue(this.draftData.new.title);
                  this.angForm.get('message').setValue(this.draftData.new.communications[0].message);
                }
              }
            });
        });
    } else {
      this.angForm = this.fb.group({
        priorityId: new FormControl(null, [
          Validators.required,
        ]),
        newStatus: new FormControl(null, [
        ]),
        expectedTime: new FormControl(null, [
          Validators.required,
        ]),
        fileInput: new FormControl('', [
        ]),
        solutionDocument: new FormControl('', [
        ]),
        message: new FormControl('', [
          Validators.required,
        ]),
      });
      this.miscService.prioritiesService({ "priorityType": "INCIDENT", "sort": "name,asc" })
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
      //console.log(this.angForm);
      // prepare incident  object
      // let selected = null;
      // let index = null;
      // let val = null;
      // let watchers = $('#watchList').val();
      // for (let k in watchers) {
      //   selected = watchers[k].split(":");
      //   index = selected[0];
      //   val = selected[1].replace(/^\s+/, "").replace(/['"]+/g, '');
      //   console.log(index + '==' + val);
      //   watchers[k] = val;
      // }
      if (sts === "DRAFT") {
        let returnPath = '/incident/user/all';
        if (this.referer === 'agent-all') {
          returnPath = '/incident/agent/all';
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
        if (this.newEntity) {
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
          this.clientStorageService.set(AppUtility.APP_INCIDENT_DRAFT_KEY, JSON.stringify(this.draftData));
          this.router.navigate([returnPath]);
        } else {
          ic = new IncidentCommunication();
          ic.message = this.angForm.controls['message'].value;
          if (this.hasAgentPermission) {
            inc.priority = priority;
            inc.status = this.angForm.controls['newStatus'].value;
          }
          inc.communications.push(ic);
          inc.id = this.incident.id;
          inc.expectedTime = this.angForm.controls['expectedTime'].value;
          if (this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY)) {
            this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
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
          this.clientStorageService.set(AppUtility.APP_INCIDENT_DRAFT_KEY, JSON.stringify(this.draftData));
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
        if (this.newEntity) {
          this.incident.moduleId = this.angForm.controls['moduleId'].value;
          this.incident.subModuleId = this.angForm.controls['subModuleId'].value;
          this.incident.title = this.angForm.controls['title'].value;
          this.incident.watchList = this.angForm.controls['watchList'].value;
          this.incident.assignedUser = null;
          this.incident.raisedUser = null;
          this.incident.priority = priority;
          this.incident.status = this.defaultStatus;
        } else {
          this.incident.priority = priority;
          this.incident.expectedTime = this.angForm.controls['expectedTime'].value;
          if (this.angForm.controls['newStatus'].value != null && this.angForm.controls['newStatus'].value != '') {
            this.incident.status = this.angForm.controls['newStatus'].value;
          }
          if (this.hasAgentPermission && this.canEdit) {
            let savedTimeEntries: TimeEntry[] = JSON.parse(this.clientStorageService.get(this.incident.id.toString()));
            if (savedTimeEntries != null) {
              for (let i = 0; i < savedTimeEntries.length; i++) {
                savedTimeEntries[i].newEntry = false;
              }
            }
            this.incident.timeEntries = savedTimeEntries == null ? [] : (savedTimeEntries.length == 0) ? [] : savedTimeEntries;
            this.clientStorageService.remove(this.incident.id.toString());
          }
        }
        // prepare incidentCommunication object
        this.incidentCommunication = new IncidentCommunication();
        this.incidentCommunication.message = this.angForm.controls['message'].value;
        this.incidentCommunication.communicatedBy = null;
        this.incidentCommunication.incident = null;
        this.incident.communications.push(this.incidentCommunication);
        this.incident.organisation = { id: this.loggedInUserService.getLoggedInUser().organisationId };
        //console.log(this.incident);
        this.callSaveIncidentService();
      }
    } else {
      console.log("Invalid Form!");
    }
  }

  getFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      this.selectedFiles = null;
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

  getSolutionDocumentFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      this.selectedFiles = null;
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angForm.controls['solutionDocument'];
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
          this.allSelectedSolutionDocumentFiles.push(this.selectedFiles[i]);
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
    this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
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
      this.clientStorageService.set(AppUtility.APP_INCIDENT_DRAFT_KEY, JSON.stringify(this.draftData));
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

  removeSolutionDocumentFile(indx) {
    if (indx > -1) { // only splice array when item is found
      this.allSelectedSolutionDocumentFiles.splice(indx, 1); // 2nd parameter means remove one item only
      $(function () {
        $('#attached-sol-file-' + indx).remove();
      });
    }
  }

  callSaveIncidentService() {
    let returnPath = '/incident/user/all';
    if (this.referer === 'agent-all') {
      returnPath = '/incident/agent/all';
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
        }
        if (typeof this.allSelectedSolutionDocumentFiles != "undefined") {
          if (this.allSelectedSolutionDocumentFiles.length > 0) {
            const formData: FormData = new FormData();
            for (var i = 0; i < this.allSelectedSolutionDocumentFiles.length; i++) {
              formData.append("file", this.allSelectedSolutionDocumentFiles[i]);
            }
            let headers = new Headers();
            headers.append('Content-Type', 'multipart/form-data');
            headers.set('Accept', 'application/json');
            let incidentId = data.id;
            this.mediaService
              .saveMediaService(incidentId, EntityType.INCIDENT, MediaType.INCIDENT_SOLUTION_DOCUMENT, "NA", formData, { 'headers': headers })
              .subscribe((data: any) => {
                this.router.navigate([returnPath]);
              });
          } else {
            this.router.navigate([returnPath]);
          }
        }
        this.router.navigate([returnPath]);
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
        this.incident.expectedTime = data.expectedTime;
        this.incident.moduleName = data.moduleName;
        this.incident.subModuleName = data.subModuleName;
        this.incident.communications = data.communications;
        this.incidentCommunications = data.communications;
        this.incident.asset = data.asset;
        this.incident.assetApproved = data.assetApproved;
        this.incident.feedbackProvided = data.feedbackProvided;
        this.incident.timeEntries = data.timeEntries;
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
          //console.log("assigned agent who can edit, after assignment");
          this.canEdit = true;
          this.hasAgentPermission = true;
          //this.populateSubmodule({ moduleId: this.incident.moduleId });
        } else if (this.incident.assignedUser != null && this.loggedInUserService.hasPermissionById(this.incident.moduleId, 'SOLVE') && this.loggedInUserService.hasPermissionById(this.incident.subModuleId, 'SOLVE')) {
          // for agent lead/manager who can edit, after assignment
          //console.log("agent lead/manager who can edit, after assignment");
          this.canEdit = true;
          this.hasAgentPermission = true;
          //this.populateSubmodule({ moduleId: this.incident.moduleId });
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
        this.angForm.get('expectedTime').setValue(this.incident.expectedTime);
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
        if (this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY)) {
          this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
          if (this.draftData.existing.length > 0) {
            for (let k in this.draftData.existing) {
              if (this.draftData.existing[k].id == this.entityId) {
                if (this.hasAgentPermission) {
                  this.angForm.get('priorityId').setValue(this.draftData.existing[k].priority.id);
                  this.angForm.get('newStatus').setValue(this.draftData.existing[k].status);
                  this.angForm.get('expectedTime').setValue(this.draftData.existing[k].expectedTime);
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

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    if (typeof moduleId !== 'undefined') {
      let c = 0;
      this.subModuleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].moduleParentId == moduleId.moduleId && this.permissions[i].licenseType == 'INCIDENT') {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
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
      //console.log(moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT));
      //console.log(moment(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT));
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  makeTitleCase(str: string): string {
    if (str != '' && typeof str != "undefined") {
      //return AppUtility.toTitleCase(str);
      return str;
    }
    return '';
  }

  resetFile(element) {
    element.value = "";
  }

  @ViewChild("newStatus") newStatus;
  triggerSolutionDocumentAttachment(newStatus) {
    if (typeof newStatus !== 'undefined') {
      if (this.canEdit && this.hasAgentPermission && newStatus.key == 'CLOSED') {
        this.solutionDocumentEnabled = true;
      } else {
        this.solutionDocumentEnabled = false;
      }
    }
  }

  addTimeEntry() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-lg',
    };
    let savedTimeEntries: TimeEntry[] = JSON.parse(this.clientStorageService.get(this.incident.id.toString()));
    const initialState = {
      timeEntries: (savedTimeEntries == null) ? (this.incident.timeEntries == null) ? [] : this.incident.timeEntries : savedTimeEntries,
      incidentNo: this.incident.incidentNo,
      incidentId: this.incident.id,
    };
    this.modalRef = this.modalService.show(AddTimeEntryComponent,
      Object.assign({}, config, { initialState })
    );
  }

  viewTimeEntry() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-lg',
    };
    const initialState = {
      timeEntries: this.incident.timeEntries,
      incidentNo: this.incident.incidentNo,
      incidentId: this.incident.id,
    };
    this.modalRef = this.modalService.show(ViewTimeEntryComponent,
      Object.assign({}, config, { initialState })
    );
  }

}