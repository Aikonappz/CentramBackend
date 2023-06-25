import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Status } from '../../model/enumerator/Status';
import { LocationVO } from '../../model/LocationVO';
import { Priority } from '../../model/Priority';
import { PriorityDataSource } from '../../service/datasource/PriorityDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-priority',
  templateUrl: './priority.component.html',
  styleUrls: ['./priority.component.scss']
})
export class PriorityComponent implements OnInit {
  moduleName: string = "PRIORITY";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['account', 'name', 'description', 'sla', 'status', 'action'];
  private datasource: PriorityDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  type: string;
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
      this.type = this.route.snapshot.paramMap.get('type');
      console.log(this.type);
      if (this.type == "asset") {
        this.displayedColumns = ['account', 'name', 'description', 'sla', 'status', 'action'];
      } else {
        this.displayedColumns = ['account', 'name', 'description', 'sla', 'status', 'action'];
      }
      this.datasource = new PriorityDataSource(this.service);
      this.datasource.loadData(0, 10, { priorityType: this.type.toUpperCase() });
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

  edit(loc: LocationVO) {
    this.router.navigate(['/master/priority/' + this.type + '/edit/' + loc.id]);
  }
  add() {
    this.router.navigate(['/master/priority/' + this.type + '/add']);
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

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

}
