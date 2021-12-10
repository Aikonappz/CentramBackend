import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { Incident } from '../../model/Incident';
import { Permission } from '../../model/Permssion';
import { IncidentDataSource } from '../../service/datasource/IncidentDataSource';
import { IncidentService } from '../../service/IncidentService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
declare var $: any;

@Component({
  selector: 'app-incident',
  templateUrl: './incident.component.html',
  styleUrls: ['./incident.component.scss']
})
export class IncidentComponent implements OnInit {
  displayedColumns = ['inc', 'slaAt', 'status', 'action'];
  private datasource: IncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: string[];
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  angForm: FormGroup;
  searchedData: Object = {};
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: IncidentService,
    private loggedInUserService: LoggedInUserService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.statusList = Object.values(IncidentStatus)
      .filter((value) => typeof value === "string" && value != 'ALL')
      .map((value) => (value as string));
    this.angForm = this.fb.group({
      incidentNo: new FormControl('', [
      ]),
      title: new FormControl('', [
      ]),
      status: new FormControl(null, [
      ]),
    });
    this.permissions = this.loggedInUserService.getModulePermissions();
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null) {
        this.moduleList.push(this.permissions[i]);
      }
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
    this.datasource = new IncidentDataSource(this.service);
    this.datasource.loadData();
  }

  ngAfterViewInit() {
    this.datasource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();

    this.paginator.page
      .pipe(
        tap(() => this.loadData())
      )
      .subscribe();
  }

  edit(inc: Incident) {
    this.router.navigate(['/incident/edit/' + inc.id]);
  }

  reopen(inc: Incident) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let ids = [];
      ids.push(inc.id);
      this.service.changeIncidentStatusService([inc.id], 'OPEN')
        .subscribe((data: any) => {
          $("#id-reopen-" + inc.id).addClass('d-none');
          $("#id-note-" + inc.id).removeClass('d-none');
          $("#id-status-" + inc.id).text('OPEN');
        });
    }
  }

  add() {
    this.router.navigate(['/incident/raise']);
  }

  loadData(req?: Object) {
    //console.log(req);
    if (this.searchedData.hasOwnProperty('incidentNo')) {
      req = this.searchedData;
    }
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }
  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  loadPage() {
    this.angForm.reset();
    this.loadData({});
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      let title = this.angForm.controls['title'].value;
      let status = this.angForm.controls['status'].value;
      let incidentNo = this.angForm.controls['incidentNo'].value;
      this.searchedData = {
        "title": title == null ? '' : title,
        "status": status == null ? '' : status,
        "incidentNo": incidentNo == null ? '' : incidentNo,
      };
      this.loadData(this.searchedData);
      //console.log(JSON.stringify(this.org));
    } else {
      console.log("Invalid Form!");
    }
  }

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

}
