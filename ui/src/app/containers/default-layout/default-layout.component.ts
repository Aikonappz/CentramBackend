import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import * as moment from 'moment';
import { interval } from 'rxjs';
import { mergeMap, tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { NotificationType } from '../../model/enumerator/NotificationType';
import { Status } from '../../model/enumerator/Status';
import { NotificationList, Notification } from '../../model/Notification';
import { Permission } from '../../model/Permssion';
import { User } from '../../model/User';
import { NotificationDataSource } from '../../service/datasource/NotificationSource';
import { MiscService } from '../../service/MiscService';
import { navItems } from '../../_nav';

@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html'
})
export class DefaultLayoutComponent implements OnInit {
  private notification: Notification;
  private notifications: Notification[] = [];
  private scrollCount: number;
  private lastFetched: number = null;
  private notificationMenuOpened: boolean = false;

  public sidebarMinimized = false;
  public navItems = navItems;
  public newNavItems = [];
  public appUrl;
  public appBrandName;
  public appDevName;
  public currentYear;
  permissions: Permission[];
  loggedInUser: any;
  unreadNotifications: number;
  constructor(
    private service: MiscService,
  ) {
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

    this.unreadNotifications = 0;
    this.scrollCount = 0;

    interval(10 * 60 * 1000)
      .pipe(
        mergeMap(() => this.service.notificationsService({ status: "PULLED", page: this.scrollCount, size: 10, lastFetched: this.lastFetched != null ? this.lastFetched : '' }))
      )
      .subscribe((data: NotificationList) => {
        if (data.content.length > 0) {
          data.content = data.content.concat(this.notifications);
          this.notifications = data.content;
          this.unreadNotifications = this.notifications.length;
        }
        if (this.notifications.length > 0) {
          this.lastFetched = this.notifications[0].id;
          //this.scrollCount = Math.ceil(this.notifications.length / 10);
        }
      });
  }

  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }

  ngOnInit(): void { }

  ngAfterViewInit() {
  }

  notificationCheck() {
    this.notificationMenuOpened = !this.notificationMenuOpened;
    console.log("update pulled");
    let updatedNotifications = [];
    let user: User;
    for (let k in this.notifications) {
      user = new User()
      updatedNotifications[k] = new Notification();
      updatedNotifications[k].id = this.notifications[k].id;
      updatedNotifications[k].version = this.notifications[k].version;
      updatedNotifications[k].status = Status.PULLED;
      updatedNotifications[k].notificationBody = this.notifications[k].notificationBody;
      updatedNotifications[k].notificationTitle = this.notifications[k].notificationTitle;
      updatedNotifications[k].notificationType = NotificationType[this.notifications[k].notificationType];
      user.id = this.notifications[k].user.id;
      user.version = this.notifications[k].user.version;
      updatedNotifications[k].user = user;
    }
    this.service
      .saveNotificationService(updatedNotifications, { status: "PULLED" })
      .subscribe((data: any) => {
        this.unreadNotifications = null;
      });
    return false;
  }

  @HostListener('notification-menu', ['$event'])
  onScroll(event: any) {
    // visible height + pixel scrolled >= total height 
    if (event.target.offsetHeight + event.target.scrollTop >= event.target.scrollHeight) {
      console.log("scroll event invoked!");
      // this.scrollCount++;
      // this.service
      //   .notificationsService({ page: this.scrollCount, size: 10, lastFetched: this.lastFetched })
      //   .subscribe((data: NotificationList) => {
      //     if (data.content.length > 0) {
      //       data.content = data.content.concat(this.notifications);
      //       this.notifications = data.content;
      //       this.unreadNotifications = this.notifications.length;
      //     }
      //     if (this.notifications.length > 0) {
      //       this.lastFetched = this.notifications[0].id;
      //       //this.scrollCount = Math.ceil(this.notifications.length / 10);
      //     }
      //   });
    }
  }

}