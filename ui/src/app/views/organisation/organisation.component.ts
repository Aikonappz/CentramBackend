import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { Status } from '../../model/enumerator/Status';
import { Organisation } from '../../model/Organisation';
import { OrganisationDataSource } from '../../service/datasource/OrganisationDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { OrganisationService } from '../../service/OrganisationService';

@Component({
  selector: 'app-organisation',
  templateUrl: './organisation.component.html',
  styleUrls: ['./organisation.component.scss']
})
export class OrganisationComponent implements OnInit {
  moduleName: string = "ORGANISATION";
  actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'addressDtl', 'licence', 'status', 'action'];
  private datasource: OrganisationDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  org: Organisation;
  defaultStatus: any = 'ALL';
  statusFlag: boolean = true;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: OrganisationService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      orgName: new FormControl('', [
        Validators.maxLength(255)
      ]),
      status: new FormControl('ALL', [

      ]),
    });
    this.org = new Organisation();
    this.org.status = this.defaultStatus;
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
    this.datasource = new OrganisationDataSource(this.service);
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
        tap(() => this.loadData({}))
      )
      .subscribe();
  }

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  updateStatus(org: Organisation) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + org.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updateStatusService([org.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }
  edit(org: Organisation) {
    this.router.navigate(['/organisation/edit/' + org.id]);
  }
  add() {
    this.router.navigate(['/organisation/add']);
  }
  loadPage() {
    this.angForm.reset();
    this.loadData({});
    this.org.status = this.defaultStatus;
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.org.name = this.angForm.controls['orgName'].value;
      this.loadData({ "name": this.org.name == null ? '' : this.org.name, "status": this.org.status });
      //console.log(JSON.stringify(this.org));
    } else {
      console.log("Invalid Form!");
    }
  }

  @ViewChild("status") status;
  onChange(inp: string) {
    let val: any = inp;
    this.org.status = val;
  }
}
