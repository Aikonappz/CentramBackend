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
declare var $: any;

@Component({
  selector: 'app-editincident',
  templateUrl: './editincident.component.html',
  styleUrls: ['./editincident.component.scss']
})
export class EditIncidentComponent implements OnInit {
  moduleName: string = "MY INCIDENTS,MY GROUP INCIDENTS";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'OPEN';
  statusFlag: boolean = true;
  entityId: number;
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  priorities: Priority[] = [];
  roles: Role[] = [];
  users: UserVO[] = [];
  incident: Incident;
  incidentCommunication: IncidentCommunication;
  incidentCommunications: IncidentCommunication[] = [];
  statusList: any = [];
  selectedFiles?: FileList;
  angForm: FormGroup;
  ckeditorToolbarConfig: any;
  readOnlyckeditorToolbarConfig: any;
  hasAgentPermission: boolean;
  mngrDtl: UserVO;
  draftData: any = { "new": null, "existing": [] };
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
    //this.ckeditorToolbarConfig = AppUtility.EDITOR_CONFIG;
    //this.readOnlyckeditorToolbarConfig = AppUtility.EDITOR_CONFIG;
    //this.readOnlyckeditorToolbarConfig.readOnly = true;
    //console.log(this.ckeditorToolbarConfig);
    //console.log(this.readOnlyckeditorToolbarConfig);
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
    for (let item in IncidentStatus) {
      if (item != "ALL") {
        this.statusList.push({ "key": item, "value": IncidentStatus[item] });
      }
    }
    this.statusList.sort(function (a, b) {
      if (b.key > a.key) return -1;
      if (a.key > b.key) return 1;
      return 0;
    });
    this.permissions = this.loggedInUserService.getModulePermissions();
    let p;
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null) {
        p = new Permission(this.permissions[i]);
        p.customerModuleName = AppUtility.toTitleCase(p.customerModuleName);
        this.moduleList.push(p);
      }
    }
    if (!this.route.snapshot.paramMap.has('id')) {
      this.angForm = this.fb.group({
        moduleId: new FormControl('', [
          Validators.required,
        ]),
        subModuleId: new FormControl('', [
          Validators.required,
        ]),
        priorityId: new FormControl('', [
          Validators.required,
        ]),
        title: new FormControl('', [
          Validators.required,
          Validators.maxLength(255),
        ]),
        watchList: new FormControl('', [
        ]),
        fileInput: new FormControl(null, [
          //Validators.required,
        ]),
        message: new FormControl('', [
          Validators.required,
        ]),
      });
      this.miscService.prioritiesService({ "sort": "name,asc" })
        .subscribe((result: PriorityList) => {
          this.priorities = result.content;
          this.userService.getUsersService()
            .subscribe((result: UserVOListResponse) => {
              let logedinUser = this.loggedInUserService.getLoggedInUser();
              this.users = [];
              for (let i = 0; i < result.content.length; i++) {
                if (logedinUser.userId != result.content[i].id)
                  this.users.push(result.content[i]);
              }
              if (this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY)) {
                this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
                this.populateSubmodule(this.draftData.new.moduleId);
                this.angForm.get('moduleId').setValue(this.draftData.new.moduleId);
                this.angForm.get('subModuleId').setValue(this.draftData.new.subModuleId);
                this.angForm.get('priorityId').setValue(this.draftData.new.priority.id);
                this.angForm.get('watchList').setValue(this.draftData.new.watchList.map(String));
                this.angForm.get('title').setValue(this.draftData.new.title);
                this.angForm.get('message').setValue(this.draftData.new.communications[0].message);
              }
              this.preapareSelect();
            });
        });
    } else {
      this.angForm = this.fb.group({
        priorityId: new FormControl('', [
          Validators.required,
        ]),
        status: new FormControl('', [
          Validators.required,
        ]),
        fileInput: new FormControl(null, [
          //Validators.required,
        ]),
        message: new FormControl('', [
          Validators.required,
        ]),
      });
      this.miscService.prioritiesService({ "sort": "name,asc" })
        .subscribe((result: PriorityList) => {
          this.priorities = result.content;
          this.userService.getUsersService()
            .subscribe((result: UserVOListResponse) => {
              this.users = result.content;
            });
        });
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.callIncidentService(this.entityId);
      if (this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY)) {
        this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
        for (let k in this.draftData.existing) {
          if (this.draftData.existing[k].id == this.entityId) {
            this.angForm.get('message').setValue(this.draftData.existing[k].communications[0].message);
            break;
          } else {
            continue;
          }
        }
      }
    }
  }

  preapareSelect() {
    $(document).ready(function () {
      let v = [];
      $('#watchList').selectize({
        //maxItems: 3
        onItemAdd: function (value, $item) {
          let selected = value.split(":");
          let index = selected[0];
          let val = selected[1].replace(/^\s+/, "").replace(/['"]+/g, '');
          // var input = $('#watchList');
          // let v = input.val();
          // v.push(val);
          // input.val(v);
          // input.trigger('input'); // Use for Chrome/Firefox/Edge
          // input.trigger('change'); // Use for Chrome/Firefox/Edge + IE11
        },
        onItemRemove: function (value) {
          let selected = value.split(":");
          let index = selected[0];
          let val = selected[1].replace(/^\s+/, "").replace(/['"]+/g, '');
          console.log(index + '==' + val);
        }
      });
      // $('#moduleId').selectize({
      //   maxItems: 1
      // });
    })
  }

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
      let selected = null;
      let index = null;
      let val = null;
      let watchers = $('#watchList').val();
      for (let k in watchers) {
        selected = watchers[k].split(":");
        index = selected[0];
        val = selected[1].replace(/^\s+/, "").replace(/['"]+/g, '');
        console.log(index + '==' + val);
        watchers[k] = val;
      }
      if (sts === "DRAFT") {
        let returnPath = '/incident/user';
        if (this.hasAgentPermission) {
          returnPath = '/incident/agent';
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
          inc.watchList = watchers;
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
          inc.communications.push(ic);
          inc.id = this.incident.id;
          if (this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY)) {
            this.draftData = JSON.parse(this.clientStorageService.get(AppUtility.APP_INCIDENT_DRAFT_KEY));
            for (let k in this.draftData.existing) {
              if (this.draftData.existing[k].id == this.incident.id) {
                this.draftData.existing[k] = inc;
                break;
              } else {
                continue;
              }
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
          this.incident.watchList = watchers;
          this.incident.assignedUser = null;
          this.incident.raisedUser = null;
          this.incident.priority = priority;
          this.incident.status = this.defaultStatus;
        } else {
          this.incident.priority = priority;
          this.incident.status = this.angForm.controls['status'].value;
        }
        // prepare incidentCommunication object
        this.incidentCommunication = new IncidentCommunication();
        this.incidentCommunication.message = this.angForm.controls['message'].value;
        this.incidentCommunication.communicatedBy = null;
        this.incidentCommunication.incident = null;
        this.incident.communications.push(this.incidentCommunication);
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
      let validMimeTpes = ["text/plain", "application/x-msexcel", "application/x-excel", "application/vnd.ms-excel", "application/excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingm", "image/jpeg", "image/pjpeg", "image/png"];
      if (!validMimeTpes.includes(type)) {
        file.setErrors({ validAttachments: true, mustBeLessThan2MB: false });
      } else if (size > (3145728)) {
        file.setErrors({ validAttachments: false, mustBeLessThan2MB: true });
      } else {
        file.setErrors(null);
        this.selectedFiles = event.target.files;
      }

      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");
    }
  }

  goBack() { this._location.back(); }

  callSaveIncidentService() {
    let returnPath = '/incident/user';
    if (this.hasAgentPermission) {
      returnPath = '/incident/agent';
    }
    this.incidentService
      .saveIncidentService(this.incident)
      .subscribe((data: Incident) => {
        //console.log(data);
        this.draftData = { "new": null, "existing": [] };
        this.clientStorageService.remove(AppUtility.APP_INCIDENT_DRAFT_KEY);
        if (typeof this.selectedFiles != "undefined") {
          if (this.selectedFiles.length > 0) {
            const formData: FormData = new FormData();
            for (var i = 0; i < this.selectedFiles.length; i++) {
              formData.append("file", this.selectedFiles[i]);
            }
            let headers = new Headers();
            headers.append('Content-Type', 'multipart/form-data');
            headers.set('Accept', 'application/json');
            let commId = data.communications[0].id;
            this.mediaService
              .saveMediaService(commId, EntityType.INCIDENT, MediaType.INCIDENT_COMMUNICATION, formData, { 'headers': headers })
              .subscribe((data: any) => {
                this.router.navigate([returnPath]);
              });
          } else {
            this.router.navigate([returnPath]);
          }
        } else {
          this.router.navigate([returnPath]);
        }
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
        this.incident.communications = data.communications;
        this.incidentCommunications = data.communications;
        this.populateSubmodule(this.incident.moduleId);

        if (
          (
            this.loggedInUserService.hasPermissionById(this.incident.moduleId, 'SOLVE')
            &&
            this.loggedInUserService.hasPermissionById(this.incident.subModuleId, 'SOLVE')
          )
          ||
          (
            this.loggedInUserService.hasPermissionById(this.incident.moduleId, 'RAISE INCIDENT')
            &&
            this.loggedInUserService.hasPermissionById(this.incident.subModuleId, 'RAISE INCIDENT')
          )
        ) {
          console.log("has permission!");
        } else {
          this.goBack();
        }

        for (let k in this.incident.communications) {
          this.incident.communications[k].communicatedBy.status = Status[this.incident.communications[k].communicatedBy.status];
        }
        if (this.incident.assignedUser != null) {
          this.incident.assignedUser.status = Status[this.incident.assignedUser.status];
        }
        if (this.incident.raisedUser != null) {
          this.incident.raisedUser.status = Status[this.incident.raisedUser.status];
        }
        // this.angForm.get('moduleId').setValue(this.incident.moduleId);
        // this.angForm.get('subModuleId').setValue(this.incident.subModuleId);
        // this.angForm.get('title').setValue(this.incident.title);
        // this.angForm.get('watchList').setValue(this.incident.watchList.map(String));

        //permission section
        if (
          this.loggedInUserService.hasPermissionById(this.incident.moduleId, 'SOLVE')
          &&
          this.loggedInUserService.hasPermissionById(this.incident.subModuleId, 'SOLVE')
        ) {
          this.hasAgentPermission = true;
        } else {
          this.hasAgentPermission = false;
        }
        this.angForm.get('priorityId').setValue(this.incident.priority.id);
        this.angForm.get('status').setValue(this.incident.status);
        //console.log(JSON.stringify(this.incident));
        //this.angForm.markAllAsTouched();
        this.userService
          .getUserService(this.incident.raisedUser.managerId)
          .subscribe((data: any) => {
            this.mngrDtl = data;
          });
      });
  }

  // @ViewChild("status") status;
  // onChange(status: boolean, inp: string) {
  //   //this.statusFlag = this.active_status.nativeElement.checked;
  //   //console.log(status);
  //   //console.log(inp);
  //   this.statusFlag = status;
  // }

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    let c = 0;
    if (moduleId != "") {
      this.subModuleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].moduleParentId == moduleId) {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
    }
  }

  downloadFile(idFile: number) {
    //window.alert(idFile);
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

}