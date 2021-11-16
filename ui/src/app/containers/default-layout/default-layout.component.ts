import { Component } from '@angular/core';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { ModulePermission } from '../../model/ModulePermission';
import { navItems } from '../../_nav';

@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html'
})
export class DefaultLayoutComponent {

  public sidebarMinimized = false;
  public navItems = navItems;
  public newNavItems = [];
  public appUrl;
  public appBrandName;
  public appDevName;
  public currentYear;
  loggedInUser: any;
  modulePermission: ModulePermission = new ModulePermission();
  modulePermissions: ModulePermission[] = [];

  constructor() {
    let m = moment();
    this.appUrl = AppUtility.APP_URL;
    this.appBrandName = AppUtility.APP_BRAND_NAME;
    this.appDevName = AppUtility.APP_DEV_NAME;
    this.currentYear = m.format('YYYY');

    this.loggedInUser = JSON.parse(localStorage.getItem(AppUtility.LOGED_IN_PROFILE));
    let c = 0;
    for (var key in this.loggedInUser.modulePermissions) {
      this.modulePermission = new ModulePermission();
      this.modulePermission.module = key;
      this.modulePermission.permissions = this.loggedInUser.modulePermissions[key].split(',');
      this.modulePermissions[c] = this.modulePermission;
      c++;
    }
    localStorage.setItem(AppUtility.LOGED_IN_USER_PERMISSIONS, JSON.stringify(this.modulePermissions));
    //console.log(JSON.stringify(this.modulePermissions));
    c = 0;
    for (let i = 0; i < this.navItems.length; i++) {
      for (let j in this.modulePermissions) {
        if (this.navItems[i].name.toUpperCase() === this.modulePermissions[j].module) {
          if (this.modulePermission.permissions.includes('READ')) {
            //console.log(JSON.stringify(this.navItems[i]));
            this.newNavItems[c] = this.navItems[i];
            c++;
          }
        }
      }
    }
    //this.navItems = this.newNavItems;
    //console.log(JSON.stringify(this.newNavItems));
  }

  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }
}
