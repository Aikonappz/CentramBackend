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
//import * as jQuery from "jquery";
declare var $: any;

@Component({
  selector: 'app-raiseincident',
  templateUrl: './raiseincident.component.html',
  styleUrls: ['./raiseincident.component.scss']
})
export class RaiseIncidentComponent implements OnInit {
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
  statusList: string[];
  selectedFiles?: FileList;
  angForm: FormGroup;
  ckeditorToolbarConfig: any;
  readOnlyckeditorToolbarConfig: any;
  hasAgentPermission: boolean;
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
    //this.ckeditorToolbarConfig = AppUtility.EDITOR_CONFIG;
    //this.readOnlyckeditorToolbarConfig = AppUtility.EDITOR_CONFIG;
    //this.readOnlyckeditorToolbarConfig.readOnly = true;
    //console.log(this.ckeditorToolbarConfig);
    //console.log(this.readOnlyckeditorToolbarConfig);
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
    this.statusList = Object.values(IncidentStatus)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));
    this.permissions = this.loggedInUserService.getModulePermissions();
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null) {
        this.moduleList.push(this.permissions[i]);
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
      this.miscService.prioritiesService()
        .subscribe((result: PriorityList) => {
          this.priorities = result.content;
        });
      this.userService.getUsersService()
        .subscribe((result: UserVOListResponse) => {
          this.users = result.content;
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
      this.miscService.prioritiesService()
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
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() {
    $(function () {
      $("#accordion").accordion({
        heightStyle: "content",
        active: false,
        collapsible: true,
      });
    });
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      // prepare incident  object
      let priorityId = this.angForm.controls['priorityId'].value;
      let priority = new Priority();
      for (let i in this.priorities) {
        if (this.priorities[i].id == priorityId) {
          priority.id = priorityId;
          priority.version = this.priorities[i].version;
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
        this.incident.status = IncidentStatus[this.angForm.controls['status'].value];
      }

      // prepare incidentCommunication object
      this.incidentCommunication = new IncidentCommunication();
      this.incidentCommunication.message = this.angForm.controls['message'].value;

      this.incident.communications.push(this.incidentCommunication);
      //console.log(this.incident);
      this.callSaveIncidentService();
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
      if (file.errors && !file.errors.mustBeCSVFile && !file.errors.mustBeLessThan2MB) {
        return;
      }
      // if (file.errors && !file.errors.mustBeCSVFile && !file.errors.mustBeLessThan2MB) {
      //   return;
      // }
      // if (type != "text/csv" && size > (3145728)) {
      //   file.setErrors({ mustBeCSVFile: true, mustBeLessThan2MB: true });
      // } else if (type == "text/csv" && size > (3145728)) {
      //   file.setErrors({ mustBeCSVFile: false, mustBeLessThan2MB: true });
      // } else if (type != "text/csv" && size <= (3145728)) {
      //   file.setErrors({ mustBeCSVFile: true, mustBeLessThan2MB: false });
      // } else if (type == "text/csv" && size <= (3145728)) {
      //   file.setErrors(null);
      //   this.selectedFiles = event.target.files;
      // }
      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");

      if (size > (3145728)) {
        file.setErrors({ mustBeCSVFile: false, mustBeLessThan2MB: true });
        return;
      } else {
        file.setErrors(null);
        this.selectedFiles = event.target.files;
      }
    }
  }

  goBack() { this._location.back(); }

  callSaveIncidentService() {
    let returnPath = '/incident/raised';
    if (this.hasAgentPermission) {
      returnPath = '/incident/incoming';
    }
    this.incidentService
      .saveIncidentService(this.incident)
      .subscribe((data: Incident) => {
        //console.log(data);
        if (typeof this.selectedFiles != "undefined") {
          if (this.selectedFiles.length > 0) {
            const formData: FormData = new FormData();
            for (var i = 0; i < this.selectedFiles.length; i++) {
              formData.append("file", this.selectedFiles[i]);
            }
            let headers = new Headers();
            headers.append('Content-Type', 'multipart/form-data');
            headers.set('Accept', 'application/json');
            let commId = data.communications[data.communications.length - 1].id;
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
        this.incident.raisedAt = data.raisedAt;
        this.incident.status = data.status;
        this.incident.communications = data.communications;
        this.incidentCommunications = data.communications;
        this.populateSubmodule(this.incident.moduleId);

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
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].moduleParentId == moduleId) {
          this.subModuleList[c] = this.permissions[i];
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
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

}