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
import { FormBuilder } from '@angular/forms';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { ClientStorageService } from '../../service/ClientStorageService';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
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
  private loggedInUser: any;
  private unreadNotifications: number;
  private menuAttributes: any;
  modalRef: BsModalRef;

  timerHandler: any;
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
    let m = moment();
    this.appUrl = environment.appUrl;
    this.appBrandName = environment.appBrandName;
    this.appDevName = environment.appDevName;
    this.currentYear = m.format('YYYY');
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.loggedInUser.orgAdmin = this.loggedInUserService.hasRole("ORG_ADMIN");
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
    this.service.notificationsService({
      status: "PULLED"
    }).subscribe((data: NotificationList) => {
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
    this.clientStorageService.set(AppUtility.APP_LOGOUT_WARNING_MODAL_STATUS_KEY, "0");
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
    this.setLastAction(Date.now());
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
      if (!isTimeout && warningTimeleft - now < 0) {
        let modalOpened = (this.clientStorageService.get(AppUtility.APP_LOGOUT_WARNING_MODAL_STATUS_KEY) == "1") ? true : false;
        //console.log(modalOpened);
        if (!modalOpened) {
          this.openLogoutWarningModal();
        }
      } else if (isTimeout) {
        this.modalRef.hide();
        this.clientStorageService.set(AppUtility.APP_LOGOUT_WARNING_MODAL_STATUS_KEY, "0");
        clearInterval(this.timerHandler);
        this.router.navigate(['/sign-out']);
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
      handler: this.timerHandler
    };
    this.modalRef = this.modalService.show(LogoutWarningComponent,
      Object.assign({}, config, { initialState })
    );
    this.clientStorageService.set(AppUtility.APP_LOGOUT_WARNING_MODAL_STATUS_KEY, "1");
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

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-warning"></i> Session Warning!</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="close()">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
      <div class="card ">
        <div class="card-body">
          <div class="row">
            <div class="col">
              <h6>You are about to sign out, do you want to continue?</h6>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <div class="float-right~">
            <button type="submit" (click)="close()" class="btn btn-warning btn-sm">
              <i class="fa fa-arrow-circle-right"></i> Continue
            </button>
            <a class="btn btn-danger btn-sm" href="javascript:void(0);" (click)="signOut()">
              <i class="fa fa-lock"></i> Logout
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>`,
  providers: [
    CommonModule
  ]
})
export class LogoutWarningComponent implements OnInit {
  handler: any;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private router: Router,
    public options: ModalOptions,
    private clientStorageService: ClientStorageService,
  ) {
    this.handler = this.options.initialState.valueOf();
    //console.log(this.handler.handler);
  }
  signOut() {
    this.close();
    clearInterval(this.handler.handler);
    this.router.navigate(['/sign-out']);
  }
  close() {
    this.clientStorageService.set(AppUtility.APP_LOGOUT_WARNING_MODAL_STATUS_KEY, "0");
    this.bsModalRef.hide();
  }
  ngOnInit() { }
  ngAfterViewInit() { }
  ngAfterContentInit() { }
}