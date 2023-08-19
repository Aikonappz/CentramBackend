import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../../config/AppUtility';
import { Organisation } from '../../../model/Organisation';
import { OrganisationDataSource } from '../../../service/datasource/OrganisationDataSource';
import { OrganisationReportDataSource } from '../../../service/datasource/OrganisationReportDataSource';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { OrganisationService } from '../../../service/OrganisationService';
import { ReportService } from '../../../service/ReportService';

@Component({
  selector: 'app-adminreport',
  templateUrl: './adminreport.component.html',
  styleUrls: ['./adminreport.component.scss']
})
export class AdminReportComponent implements OnInit {
  moduleName: string = "SITE ADMIN REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'addressDtl', 'licence', 'status',];
  private datasource: OrganisationReportDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  org: Organisation;
  defaultStatus: any = 'ALL';
  statusFlag: string = 'ALL';
  drStatus: { isOpen: boolean } = { isOpen: false };
  disabled: boolean = false;
  isDropup: boolean = true;
  autoClose: boolean = false;
  searchedData: any = {};
  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: ReportService,
    private modalService: BsModalService,
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

  onHidden(): void {
    console.log('Dropdown is hidden');
  }
  onShown(): void {
    console.log('Dropdown is shown');
  }
  isOpenChange(): void {
    console.log('Dropdown state is changed');
  }

  toggleDropdown($event: MouseEvent): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.drStatus.isOpen = !this.drStatus.isOpen;
  }

  change(value: boolean): void {
    this.drStatus.isOpen = value;
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
    this.datasource = new OrganisationReportDataSource(this.service);
    this.datasource.loadData();
  }

  ngOnDestroy() {
    this.drStatus.isOpen = false;
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
  download() {
    this.service
      .downloadOrganisationReport(this.searchedData)
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: "text/csv" });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          alert('Please disable your Pop-up blocker and try again.');
        }
      });
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

  @ViewChild("status") status;
  onChange(inp: string) {
    this.statusFlag = inp;
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.org.name = this.angForm.controls['orgName'].value;
      this.org.status = this.statusFlag;
      this.searchedData = { "name": this.org.name == null ? '' : this.org.name, "status": this.org.status };
      this.loadData(this.searchedData);
      //console.log(JSON.stringify(this.org));
    } else {
      console.log("Invalid Form!");
    }
  }


}