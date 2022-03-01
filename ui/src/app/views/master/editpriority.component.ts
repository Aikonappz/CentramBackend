import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { Status } from '../../model/enumerator/Status';
import { AppUtility } from '../../config/AppUtility';
import { Priority } from '../../model/Priority';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-editpriority',
  templateUrl: './editpriority.component.html',
  styleUrls: ['./editpriority.component.scss']
})
export class EditPriorityComponent implements OnInit {
  moduleName: string = "PRIORITY";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  priority: Priority;
  timeList: any[] = [];
  nameList: any[] = [];
  angForm: FormGroup;
  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private service: MiscService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      name: new FormControl(null, [
        Validators.required,
        Validators.maxLength(255),
      ]),
      description: new FormControl(null, [
        Validators.required,
        Validators.maxLength(2000),
      ]),
      sla: new FormControl(null, [
        Validators.required,
      ]),
      status: new FormControl('ACTIVE', [
      ]),
    });
    this.priority = new Priority();
    this.priority.status = this.defaultStatus;
    if (environment.production == false) {
      this.timeList.push({ id: "00:05", label: "00:05 hrs" });
      this.timeList.push({ id: "00:07", label: "00:07 hrs" });
      this.timeList.push({ id: "00:10", label: "00:10 hrs" });
      this.timeList.push({ id: "00:15", label: "00:15 hrs" });
      this.timeList.push({ id: "00:20", label: "00:20 hrs" });
      this.timeList.push({ id: "00:25", label: "00:25 hrs" });
      this.timeList.push({ id: "00:30", label: "00:30 hrs" });
    }
    let tmList = AppUtility.getSlaList(500);
    for (let k = 0; k < tmList.length; k++) {
      if (k != 0) {
        this.timeList.push({ id: tmList[k], label: tmList[k] + " hrs" });
      }
    }
    for (let k = 1; k <= 10; k++) {
      this.nameList.push({ id: "P" + k, label: "P" + k });
    }
  }

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
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
    if (!this.route.snapshot.paramMap.has('id')) { } else {
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.newEntity = false;
      this.callGetPriorityService(this.entityId);
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      if (this.statusFlag === false) {
        let res = window.confirm("Do you really want to change the status?")
        if (!res) {
          return;
        }
      }
      //console.log(this.angForm);      
      this.priority.name = this.angForm.controls['name'].value;
      this.priority.description = this.angForm.controls['description'].value;
      this.priority.sla = this.angForm.controls['sla'].value;
      /* process department and location */
      this.priority.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user.status);
      this.callSavePriorityService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSavePriorityService() {
    this.service
      .savePriorityService(this.priority)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/master/priority']);
      });
  }

  callGetPriorityService(id: number) {
    this.service
      .priorityService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.priority.id = data.id;
        this.priority.name = data.name;
        this.priority.description = data.description;
        this.priority.sla = data.sla;
        this.priority.status = data.status;
        this.priority.version = data.version;
        //console.log(JSON.stringify(this.user));
        this.angForm.get('name').setValue(this.priority.name);
        this.angForm.get('description').setValue(this.priority.description);
        this.angForm.get('sla').setValue(this.priority.sla);
        this.statusFlag = String(this.priority.status) == 'ACTIVE' ? true : false;
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }

}