import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { Incident } from '../../model/Incident';
import { Permission } from '../../model/Permssion';
import { IncidentDataSource } from '../../service/datasource/IncidentDataSource';
import { UserAllocatedAssetDataSource } from '../../service/datasource/UserAllocatedAssetDataSource';
import { IncidentService } from '../../service/IncidentService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
declare var $: any;

@Component({
  selector: 'app-my-asset',
  templateUrl: './my-asset.component.html',
  styleUrls: ['./my-asset.component.scss']
})
export class MyAssetComponent implements OnInit {
  moduleName: string = "MY ASSET";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['inc', 'assetDtl', 'status',];
  private datasource: UserAllocatedAssetDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  statusList: any = [];
  permissions: Permission[] = [];
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  angForm: FormGroup;
  searchedData: any = { incidentType: "ASSET", assigned: 1 };
  incidentStatus: IncidentStatus;
  booleanList: any[] = [];
  mode: string = '';
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private route: ActivatedRoute,
    private service: IncidentService,
    private loggedInUserService: LoggedInUserService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
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
    this.angForm = this.fb.group({
      serialNo: new FormControl(null, [
      ]),
      deallocated: new FormControl(null, [
      ]),
    });
    this.permissions = this.loggedInUserService.getModulePermissions();
    for (let i in this.permissions) {
      if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null) {
        this.moduleList.push(this.permissions[i]);
      }
    }
    this.booleanList.push({ id: 0, label: 'No' });
    this.booleanList.push({ id: 1, label: 'Yes' });
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
    this.datasource = new UserAllocatedAssetDataSource(this.service);
    this.datasource.loadData(0, 10, this.searchedData);
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
    this.router.navigate(['/asset/user/edit/' + inc.id]);
  }

  reopen(inc: Incident) {
    let res = window.confirm("Do you really want to Reopen this incident?")
    if (res) {
      let ids = [];
      ids.push(inc.id);
      this.service.reOpenIncidentService([inc.id], 'OPEN')
        .subscribe((data: any) => {
          $("#id-reopen-" + inc.id).addClass('d-none');
          $("#id-note-" + inc.id).removeClass('d-none');
          $("#id-status-" + inc.id).text('OPEN');
        });
    }
  }

  add(mode: string) {
    this.router.navigate(['/asset/user/add/' + mode]);
  }

  loadData(req?: any) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, this.searchedData);
  }

  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(this.loggedInUserService.getLoggedInUser().timeZone).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }

  loadPage() {
    this.angForm.reset();
    this.searchedData = { incidentType: "ASSET", assigned: 1 };
    this.loadData({});
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      let serialNo = this.angForm.controls['serialNo'].value;
      let deallocated = this.angForm.controls['deallocated'].value;
      this.searchedData.serialNo = serialNo == null ? '' : serialNo;
      this.searchedData.deallocated = deallocated == null ? -1 : deallocated;
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

  canReopen(modifiedDate: Date) {
    let logedinUser = this.loggedInUserService.getLoggedInUser();
    let dateLastModified = moment(modifiedDate).tz(logedinUser.timeZone);
    let today = moment().tz(logedinUser.timeZone);
    //console.log(dateLastModified + " -- " + today + " -- " + today.diff(dateLastModified, 'days'));
    return (today.diff(dateLastModified, 'days') > 15) ? false : true;
  }

  checkRole(): string {
    let role = this.hasPermission("READ") ? "USER" : "AGENT";
    return role;
  }

}