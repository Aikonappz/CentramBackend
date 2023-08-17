import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';


import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { DashboardService } from '../../../service/DashboardService';
declare var $: any;

@Component({
  templateUrl: 'uatdashboard.component.html',
  styleUrls: ['uatdashboard.component.scss']
})
export class UATDashboardComponent implements OnInit {
  roles: string[];
  loggedInUser: LoggedInUser;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private service: DashboardService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.roles = this.loggedInUser.roles;
    //console.log(this.loggedInUser.licenseType);
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

  ngOnInit(): void { }
}