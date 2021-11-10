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
  selector: 'app-edituser',
  templateUrl: './edituser.component.html',
  styleUrls: ['./edituser.component.scss']
})
export class EditUserComponent implements OnInit {

  constructor(titleService: Title, private router: Router, private userService: UserService) {
    console.log("EditUserComponent");
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

  }

  ngAfterViewInit() {

  }
}