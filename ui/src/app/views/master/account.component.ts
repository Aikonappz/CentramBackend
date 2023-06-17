import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { tap } from 'rxjs/operators';
import { DistributionList } from '../../model/DistributionList';
import { Status } from '../../model/enumerator/Status';
import { Vendor } from '../../model/Vendor';
import { VendorDataSource } from '../../service/datasource/VendorDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  moduleName: string = "VENDOR";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'conDtl', 'allocationType', 'status', 'action'];
  datasource: VendorDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  type: string = "incident";
  hasAllocationType: boolean = true;
  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private route: ActivatedRoute,
    private service: MiscService
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
      //console.log(this.type);
      this.displayedColumns = ['name', 'conDtl', 'allocationType', 'status', 'action'];
      this.hasAllocationType = true;
      this.datasource = new VendorDataSource(this.service);
      this.datasource.loadData(0, 10, { vendorType: this.type.toUpperCase() });
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
        tap(() => this.loadData({ vendorType: this.type.toUpperCase() }))
      )
      .subscribe();
  }

  edit(dl: DistributionList) {
    this.router.navigate(['/master/account/edit/' + dl.id]);
  }

  add() {
    this.router.navigate(['/master/account/add']);
  }

  updateStatus(vendor: Vendor) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + vendor.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updatePrioritiesStatusService([vendor.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  loadData(req: any = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

}
