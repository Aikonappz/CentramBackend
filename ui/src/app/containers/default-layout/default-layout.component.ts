import { Component, HostListener, OnInit, } from '@angular/core';
import * as moment from 'moment';
import { PushNotificationsService } from 'ng-push-ivy';
import { environment } from '../../../environments/environment';
import { AppUtility } from '../../config/AppUtility';
import { Status } from '../../model/enumerator/Status';
import { NotificationList, Notification } from '../../model/Notification';
import { NotificationVO } from '../../model/NotificationVO';
import { Permission } from '../../model/Permssion';
import { User } from '../../model/User';
import { MiscService } from '../../service/MiscService';
import { NotificationService } from '../../service/NotificationService';
import { NotificationWSService } from '../../service/NotificationWSService';
import { PermissionService } from '../../service/PermissionService';
import { navItems } from '../../_nav';

@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html'
})
export class DefaultLayoutComponent implements OnInit {
  private notifications: Notification[] = [];
  private notificationMenuOpened: boolean = false;
  public sidebarMinimized = false;
  public navItems = navItems;
  public newNavItems = [];
  public appUrl;
  public appBrandName;
  public appDevName;
  public currentYear;
  private permissions: Permission[];
  private loggedInUser: any;
  private unreadNotifications: number;
  constructor(
    private service: MiscService,
    private pushNotifications: PushNotificationsService,
    private notificationService: NotificationService,
    private websocketService: NotificationWSService,
    private permissionService: PermissionService,
  ) {
    let m = moment();
    this.appUrl = environment.appUrl;
    this.appBrandName = environment.appBrandName;
    this.appDevName = environment.appDevName;
    this.currentYear = m.format('YYYY');
    this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
    this.loggedInUser.orgAdmin = this.permissionService.hasRole("ORG_ADMIN");
    //console.log(JSON.stringify(this.loggedInUser));
    this.permissions = this.loggedInUser.modulePermissions;
    this.permissions.forEach(function (itm) {
      itm.actions = itm.actionNames.split(',');
    });
    //localStorage.setItem(AppUtility.LOGED_IN_USER_PERMISSIONS, btoa(JSON.stringify(this.permissions)));
    //console.log(JSON.stringify(this.permissions));
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
    //ask to allow automatic notification
    this.pushNotifications.requestPermission();
    this.unreadNotifications = 0;
    this.service.notificationsService({
      status: "PULLED"
    }).subscribe((data: NotificationList) => {
      if (data.content.length > 0) {
        data.content = data.content.concat(this.notifications);
        this.notifications = data.content;
        this.unreadNotifications = this.notifications.length;
      }
    });
  }

  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }

  ngOnInit(): void { this.connect(); }

  ngAfterViewInit() {
  }

  connect(): void {
    this.websocketService.connect();
    // subscribe receives the value.
    this.notificationService.notificationMessage
      .subscribe((data: NotificationVO) => {
        //console.log('receive message', data);
        let n = new Notification();
        let u = new User();
        n.id = data.id;
        n.notificationTitle = data.title;
        n.notificationBody = data.body;
        n.status = data.status;
        n.notificationType = data.notificationType;
        u.id = data.userId;
        n.user = u;
        this.notifications.push(n);
        this.unreadNotifications = this.notifications.length;
      });
  }

  disconnect(): void {
    this.websocketService.disconnect();
  }

  notificationCheck() {
    this.notificationMenuOpened = !this.notificationMenuOpened;
    console.log("update pulled");
    let updatedNotifications = [];
    for (let k in this.notifications) {
      updatedNotifications.push(this.notifications[k].id);
    }
    if (updatedNotifications.length > 0) {
      this.service
        .updateNotificationsStatusService(updatedNotifications, Status.PULLED)
        .subscribe((data: any) => {
          this.unreadNotifications = null;
        });
    }
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