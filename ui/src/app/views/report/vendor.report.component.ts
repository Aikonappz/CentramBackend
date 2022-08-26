import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { ReportVendorDataSource } from '../../service/datasource/ReportVendorDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { ReportService } from '../../service/ReportService';
import { UserService } from '../../service/UserService';

@Component({
  selector: 'app-vendor-report',
  templateUrl: './vendor.report.component.html',
  styleUrls: ['./vendor.report.component.scss']
})
export class VendorReportComponent implements OnInit {
  moduleName: string = "VENDOR REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'conDtl', 'status'];
  datasource: ReportVendorDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  searchedData: any = { vendorType: "ASSET", };
  type: string;
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: ReportService,
    private loggedInUserService: LoggedInUserService,
    private miscService: MiscService,
    private route: ActivatedRoute,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
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
    this.route.params.subscribe(params => {
      this.type = this.route.snapshot.paramMap.get('licenceType');
      if (this.type == "asset") {
        this.datasource = new ReportVendorDataSource(this.service);
        this.searchedData = { vendorType: "ASSET" };
        this.datasource.loadData(0, 10, this.searchedData);
      } else if (this.type == "incident") {
        this.datasource = new ReportVendorDataSource(this.service);
        this.searchedData = { vendorType: "INCIDENT" };
        this.datasource.loadData(0, 10, this.searchedData);
      }
    });
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

  ngAfterContentInit() {
  }

  formatText(str: string) {
    return AppUtility.toTitleCase(str);
  }

  download() {
    this.service
      .downloadVendorReport(this.searchedData)
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

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, this.searchedData);
  }

}