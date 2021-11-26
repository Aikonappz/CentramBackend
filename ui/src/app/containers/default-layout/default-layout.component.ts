import { Component } from '@angular/core';
import * as moment from 'moment';
import { AppUtility } from '../../config/AppUtility';
import { Permission } from '../../model/Permssion';
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
  permissions: Permission[];
  loggedInUser: any;

  constructor() {
    let m = moment();
    this.appUrl = AppUtility.APP_URL;
    this.appBrandName = AppUtility.APP_BRAND_NAME;
    this.appDevName = AppUtility.APP_DEV_NAME;
    this.currentYear = m.format('YYYY');
    this.loggedInUser = JSON.parse(localStorage.getItem(AppUtility.LOGED_IN_PROFILE));
    this.permissions = this.loggedInUser.modulePermissions;
    this.permissions.forEach(function (itm) {
      itm.actions = itm.actionNames.split(',');
    });
    localStorage.setItem(AppUtility.LOGED_IN_USER_PERMISSIONS, JSON.stringify(this.permissions));
    //console.log(JSON.stringify(this.navItems));
    let c = 0;
    for (let i = 0; i < this.navItems.length; i++) {
      for (let j in this.permissions) {
        if (
          this.permissions[j].appModule == true &&
          this.navItems[i].name.toUpperCase() === this.permissions[j].moduleName &&
          this.permissions[j].actions.includes('READ')
        ) {
          //console.log(JSON.stringify(this.navItems[i]));
          this.newNavItems[c] = this.navItems[i];
          if (this.navItems[i].hasOwnProperty("children")) {
            if (
              this.permissions[j].appModule == true &&
              this.navItems[i].name.toUpperCase() === this.permissions[j].moduleName &&
              this.permissions[j].actions.includes('READ')
            ) {
              let parentId = this.permissions[j].moduleId;
              //console.log(this.newNavItems[c].children);
              let childMenus = [];
              for (let sm in this.newNavItems[c].children) {
                for (let k in this.permissions) {
                  if (
                    this.permissions[k].appModule == true &&
                    this.newNavItems[c].children[sm].name.toUpperCase() === this.permissions[k].moduleName &&
                    this.permissions[k].actions.includes('READ') &&
                    parentId === this.permissions[k].moduleParentId
                  ) {
                    childMenus.push(this.newNavItems[c].children[sm]);
                  }
                }
              }
              this.newNavItems[c].children = childMenus;
            }
          }
          c++;
        }
      }
    }
    this.navItems = this.newNavItems;
    //console.log(JSON.stringify(this.newNavItems));
  }

  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }
}