import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { tap } from 'rxjs/operators';
import { DistributionList } from '../../model/DistributionList';
import { Status } from '../../model/enumerator/Status';
import { Priority } from '../../model/Priority';
import { DistributionListDataSource } from '../../service/datasource/DistributionListDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-distributionlist',
  templateUrl: './distributionlist.component.html',
  styleUrls: ['./distributionlist.component.scss']
})
export class DistributionListComponent implements OnInit {
  moduleName: string = "DISTRIBUTION LIST";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'email', 'action'];
  datasource: DistributionListDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private loggedInUserService: LoggedInUserService,
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
    this.datasource = new DistributionListDataSource(this.service);
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

  edit(dl: DistributionList) {
    this.router.navigate(['/master/dl/edit/' + dl.id]);
  }
  add() {
    this.router.navigate(['/master/dl/add']);
  }

  updateStatus(prty: Priority) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + prty.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updatePrioritiesStatusService([prty.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  loadData() {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize);
  }

}
