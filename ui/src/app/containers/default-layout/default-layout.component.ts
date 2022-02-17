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
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { navItems } from '../../_nav';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { ClientStorageService } from '../../service/ClientStorageService';
import { Router } from '@angular/router';
import { LogoutWarningComponent } from './modal/LogoutWarningComponent';
import { LoggedInUser } from '../../model/LoggedInUser';
declare var $: any;

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
  private loggedInUser: LoggedInUser;
  private unreadNotifications: number;
  private menuAttributes: any;
  modalRef: BsModalRef;
  roles: string[] = [];
  userRoles: string[] = [];
  timerHandler: any;
  isProd: boolean = false;
  constructor(
    private service: MiscService,
    private pushNotifications: PushNotificationsService,
    private notificationService: NotificationService,
    private websocketService: NotificationWSService,
    private loggedInUserService: LoggedInUserService,
    private router: Router,
    private clientStorageService: ClientStorageService,
    private modalService: BsModalService,
  ) {
    //console.log("default layout...");
    this.isProd = environment.production;
    this.appUrl = environment.appUrl;
    this.appBrandName = environment.appBrandName;
    this.appDevName = environment.appDevName;
    this.currentYear = moment().format('YYYY');
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.loggedInUser.orgAdmin = this.loggedInUserService.hasRole("ORG_ADMIN");
    this.roles = this.loggedInUser.roles;
    if (this.hasUserRole()) {
      this.userRoles.push("EMP");
    }
    if (this.hasAgentRole()) {
      this.userRoles.push("AGENT");
    }
    if (this.hasCategoryAdminRole()) {
      this.userRoles.push("CATEGORY_ADMIN");
    }
    this.clientStorageService.set(AppUtility.APP_LOGGEDIN_USR_ROLES, this.userRoles);
    //console.log(JSON.stringify(this.loggedInUser));
    this.permissions = this.loggedInUser.modulePermissions;
    // this.permissions.forEach(function (itm) {
    //   itm.actions = itm.actionName.split(',');
    // });
    //console.log(JSON.stringify(this.permissions));
    let c = 0;
    for (let i = 0; i < this.navItems.length; i++) {
      this.menuAttributes = this.navItems[i].attributes;
      for (let j in this.permissions) {
        if (
          this.permissions[j].appModule == true && this.permissions[j].moduleParentId == null &&
          this.menuAttributes.moduleName === this.permissions[j].moduleName &&
          this.permissions[j].actions.includes('READ')
        ) {
          //console.log(this.menuAttributes.moduleName + " -- " + this.permissions[j].moduleName);
          this.newNavItems[c] = this.navItems[i];
          if (this.navItems[i].hasOwnProperty("children")) {
            let parentId = this.permissions[j].moduleId;
            //console.log(this.newNavItems[c].children);
            let childMenus = [];
            for (let sm in this.newNavItems[c].children) {
              for (let k in this.permissions) {
                this.menuAttributes = this.newNavItems[c].children[sm].attributes;
                if (typeof this.menuAttributes.licenceType !== 'undefined') {
                  let licences = this.menuAttributes.licenceType.split(',');
                  if (
                    licences.includes(this.loggedInUser.licenseType) &&
                    this.permissions[k].appModule == true && this.permissions[k].moduleParentId != null &&
                    this.menuAttributes.moduleName === this.permissions[k].moduleName &&
                    this.permissions[k].actions.includes('READ') &&
                    parentId === this.permissions[k].moduleParentId
                  ) {
                    //console.log(this.newNavItems[c].children[sm]);
                    childMenus.push(this.newNavItems[c].children[sm]);
                  }
                } else {
                  if (
                    this.permissions[k].appModule == true && this.permissions[k].moduleParentId != null &&
                    this.menuAttributes.moduleName === this.permissions[k].moduleName &&
                    this.permissions[k].actions.includes('READ') &&
                    parentId === this.permissions[k].moduleParentId
                  ) {
                    //console.log(this.newNavItems[c].children[sm]);
                    childMenus.push(this.newNavItems[c].children[sm]);
                  }
                }
              }
            }
            this.newNavItems[c].children = childMenus;
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
    this.service.notificationsService({ status: "PUSHED", }).subscribe((data: NotificationList) => {
      if (typeof data.content != "undefined" && data.content.length > 0) {
        data.content = data.content.concat(this.notifications);
        this.notifications = data.content;
        this.unreadNotifications = this.notifications.length;
      }
    });
    //console.log("AutoLogoutService created....");
    this.check();
    this.initListener();
    this.initInterval();
    this.clientStorageService.set(AppUtility.APP_LAST_ACTION_KEY, Date.now().toString());
  }

  initListener() {
    document.body.addEventListener('click', () => this.reset());
    document.body.addEventListener('mouseover', () => this.reset());
    document.body.addEventListener('mouseout', () => this.reset());
    document.body.addEventListener('keydown', () => this.reset());
    document.body.addEventListener('keyup', () => this.reset());
    document.body.addEventListener('keypress', () => this.reset());
  }

  reset() {
    if (this.modalService.getModalsCount() == 0) {
      this.setLastAction(Date.now());
    }
  }

  initInterval() {
    this.timerHandler = setInterval(() => {
      this.check();
    }, AppUtility.APP_ACTIVITY_CHECK_INTERVAL);
  }

  check() {
    if (!isNaN(this.getLastAction())) {
      const now = Date.now();
      const timeleft = this.getLastAction() + AppUtility.APP_NON_ACTIVITY_LOGOUT_INTERVAL * 60 * 1000;
      const warningTimeleft = this.getLastAction() + AppUtility.APP_LOGOUT_WARNING_INTERVAL * 60 * 1000;
      const diff = timeleft - now;
      const isTimeout = diff < 0;
      //console.log("timeleft => " + timeleft);
      //console.log("warningTimeleft => " + warningTimeleft);
      //TODO: need to handle setinterval
      //console.log("diff => " + diff);
      //var tempTime = moment.duration(diff);
      //var timeLeft = tempTime.hours() + ":" + tempTime.minutes() + ":" + tempTime.seconds();
      //console.log(timeLeft);
      //console.log("getModalsCount -", " ", diff, this.modalService.getModalsCount());
      if (!isTimeout && warningTimeleft - now < 0) {
        if (this.modalService.getModalsCount() == 0) {
          //this.modalOpenedFirstTime = true;
          this.openLogoutWarningModal();
        }
      } else if (isTimeout) {
        if (this.modalService.getModalsCount() > 0) {
          this.modalRef.hide();
          this.clientStorageService.set(AppUtility.APP_SESSION_TIMEOUT_KEY, "1");
          clearInterval(this.timerHandler);
          this.router.navigate(['/sign-out']);
        }
      }
    }
  }

  openLogoutWarningModal() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      handler: this.timerHandler,
    };
    this.modalRef = this.modalService.show(LogoutWarningComponent,
      Object.assign({}, config, { initialState })
    );
    this.setLastAction(this.getLastAction());
  }

  public getLastAction() {
    return parseInt(this.clientStorageService.get(AppUtility.APP_LAST_ACTION_KEY));
  }

  public setLastAction(lastAction: number) {
    this.clientStorageService.set(AppUtility.APP_LAST_ACTION_KEY, lastAction.toString());
  }

  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }

  ngOnInit(): void {
    this.connect();
    //console.log(this.clientStorageService.get(AppUtility.APP_LOGGEDIN_USR_ROLES));
    var roles = this.clientStorageService.get(AppUtility.APP_LOGGEDIN_USR_ROLES);
    $(function () {
      if (!roles.includes('EMP') && roles.includes('AGENT')) {
        $('.nav-item').removeClass('highlighted-yellow');
      }
    });
  }

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
        this.notifications.sort(function (a, b) {
          if (b.id > a.id) return 1;
          if (a.id > b.id) return -1;
          return 0;
        });
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