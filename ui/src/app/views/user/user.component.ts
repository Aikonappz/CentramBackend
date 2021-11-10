import { Component, OnInit, ViewChild } from '@angular/core';
import { UserDataSource } from '../../service/datasource/UserDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { UserVO } from '../../model/UserVO';
import { Status } from '../../model/enumerator/Status';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  displayedColumns = ['name', 'contact', 'department', 'status', 'action'];
  private userVODatasource: UserDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(titleService: Title, private router: Router, private userService: UserService) {
    console.log("UserComponent");
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
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
    this.userVODatasource = new UserDataSource(this.userService);
    this.userVODatasource.loadUserVOs();
  }

  ngAfterViewInit() {
    this.userVODatasource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();

    this.paginator.page
      .pipe(
        tap(() => this.loadUserVOs())
      )
      .subscribe();
  }

  loadUserVOs() {
    this.userVODatasource.loadUserVOs(this.paginator.pageIndex, this.paginator.pageSize);
  }

  updateStatus(user: UserVO) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + user.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.userService
        .updateStatusService([user.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  editUser(user: UserVO) {
    this.router.navigate(['/user/edit/' + user.id]);
  }
  addUser() {
    this.router.navigate(['/user/add']);
  }
}