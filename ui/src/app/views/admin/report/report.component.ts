import { Component, OnInit, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { AppUtility } from '../../../config/AppUtility';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {
  moduleName: string = "SITE ADMIN REPORT";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  roles: string[] = [];
  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private route: ActivatedRoute
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.roles = this.loggedInUserService.getLoggedInUser().roles;
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
    if (this.roles.includes('APP_BUSINESS_LEAD') || this.roles.includes('APP_ADMIN')) {
      this.router.navigate(['/report/admin-report']);
    } else if (this.roles.includes('ORG_ADMIN') || this.hasAgentRole() || this.hasCategoryAdminRole()) {
      this.router.navigate(['/report/incident-report']);
    } else if (this.hasUserRole()) {
      this.router.navigate(['/' + AppUtility.EXPLORE_LANDING_PAGE_PATH]);
    }
  }

  ngOnDestroy() {
  }

  ngAfterViewInit() {
  }

  hasUserRole() {
    for (let k in this.roles) {
      if (this.roles[k].match(/.*_USER_.*/)) {
        return true
      }
    }
    return false;
  }

  hasAgentRole() {
    for (let k in this.roles) {
      if (this.roles[k].match(/.*_AGENT_.*/)) {
        return true
      }
    }
    return false;
  }

  hasCategoryAdminRole() {
    for (let k in this.roles) {
      if (this.roles[k].match(/.*_CATEGORY_ADMIN.*/)) {
        return true
      }
    }
    return false;
  }
}