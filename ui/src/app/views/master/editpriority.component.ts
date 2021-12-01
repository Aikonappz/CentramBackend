import { Component, OnInit, ViewChild, } from '@angular/core';

import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { Status } from '../../model/enumerator/Status';
import { AppUtility } from '../../config/AppUtility';
import { Priority } from '../../model/Priority';

@Component({
  selector: 'app-editpriority',
  templateUrl: './editpriority.component.html',
  styleUrls: ['./editpriority.component.scss']
})
export class EditPriorityComponent implements OnInit {
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  prty: Priority;
  timeList: string[] = [];
  nameList: string[] = [];
  angForm: FormGroup;
  constructor(
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
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
      ]),
      description: new FormControl('', [
        Validators.required,
        Validators.maxLength(2000),
      ]),
      sla: new FormControl('', [
        Validators.required,
      ]),
      status: new FormControl('ACTIVE', [
      ]),
    });
    this.prty = new Priority();
    this.prty.status = this.defaultStatus;
    this.timeList = AppUtility.getSlaList(500);
    for (let k = 1; k <= 10; k++) {
      this.nameList.push("P" + k);
    }
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
    if (!this.route.snapshot.paramMap.has('id')) {

    } else {
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
      //console.log(this.angForm);      
      this.prty.name = this.angForm.controls['name'].value;
      this.prty.description = this.angForm.controls['description'].value;
      this.prty.sla = this.angForm.controls['sla'].value;
      /* process department and location */
      this.prty.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user.status);
      this.callSavePriorityService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSavePriorityService() {
    this.service
      .savePriorityService(this.prty)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/masters/priority']);
      });
  }

  callGetPriorityService(id: number) {
    this.service
      .priorityService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.prty.id = data.id;
        this.prty.name = data.name;
        this.prty.description = data.description;
        this.prty.sla = data.sla;
        this.prty.status = data.status;
        this.prty.version = data.version;
        //console.log(JSON.stringify(this.user));

        this.angForm.get('name').setValue(this.prty.name);
        this.angForm.get('description').setValue(this.prty.description);
        this.angForm.get('sla').setValue(this.prty.sla);

        this.statusFlag = String(this.prty.status) == 'ACTIVE' ? true : false;
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }

}