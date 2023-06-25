import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { tap } from 'rxjs/operators';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { AccountDataSource } from '../../service/datasource/AccountDataSource';
import { Account } from '../../model/Account';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  moduleName: string = "ACCOUNT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'conDtl', 'allocationType', 'status', 'action'];
  datasource: AccountDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
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
      this.datasource = new AccountDataSource(this.service);
      this.datasource.loadData(0, 10, {});
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
        tap(() => this.loadData({}))
      )
      .subscribe();
  }

  edit(account: Account) {
    this.router.navigate(['/master/account/edit/' + account.id]);
  }

  add() {
    this.router.navigate(['/master/account/add']);
  }

  loadData(req: any = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

}
