import { Component, HostListener, OnInit, ViewChild, } from '@angular/core';
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
import { Subscription } from 'rxjs';
import { ChatRoomService } from '../../service/ChatRoomService';
import { ChatWSService } from '../../service/ChatWSService';
import { ChatService } from '../../service/ChatService';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ChatMessage } from '../../model/ChatMessage';
import { EntityType } from '../../model/enumerator/EntityType';
import { MediaService } from '../../service/MediaService';
import { MediaType } from '../../model/enumerator/MediaType';
import { HttpClient } from '@angular/common/http';
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
  chatTimerHandler: any;
  isProd: boolean = false;
  chatSelection: boolean = false;
  chatSessionActive: boolean = false;
  chatInitiator: number = null;

  /*Chat specific*/
  subscription: Subscription;
  chatRoomId: string;;
  chatHeaderTxt: string = "Message";
  angChatForm = new FormGroup({
    chatMessage: new FormControl('', [
      //Validators.required,
    ]),
    chatRoomId: new FormControl('', [
      //Validators.required,
    ]),
    fileInput: new FormControl('', [
    ]),
  });
  chatTimeLeft: string;
  selectedFiles?: FileList;
  /*Chat specific*/

  canAssign: boolean;
  angFormAssign: FormGroup;
  moduleList: Permission[] = [];
  subModuleList: Permission[];
  parentModuleList: any[] = [];
  canCommunate: boolean = false;

  constructor(
    private fb: FormBuilder,
    private service: MiscService,
    private pushNotifications: PushNotificationsService,
    private notificationService: NotificationService,
    private websocketService: NotificationWSService,
    private loggedInUserService: LoggedInUserService,
    private router: Router,
    private clientStorageService: ClientStorageService,
    private modalService: BsModalService,
    private chatRoomService: ChatRoomService,
    private chatWSSocketService: ChatWSService,
    private chatService: ChatService,
    private miscService: MiscService,
    private mediaService: MediaService,
    private http: HttpClient,
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
                    licences.includes(this.loggedInUser.licenseType)
                    && this.permissions[k].appModule == true
                    && this.permissions[k].moduleParentId != null
                    && parentId === this.permissions[k].moduleParentId
                    &&
                    (
                      (this.menuAttributes.moduleName === this.permissions[k].moduleName
                        && this.permissions[k].actions.includes('READ'))
                    )
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

    this.angFormAssign = this.fb.group({
      parentModule: new FormControl(null, [
        Validators.required,
      ]),
      moduleId: new FormControl(null, [
        Validators.required,
      ]),
      subModuleId: new FormControl(null, [
        Validators.required,
      ]),
      message: new FormControl(null, [
        Validators.required,
      ]),
    });

    this.parentModuleList.push({ id: 'ASSET', label: 'Asset' });
    this.parentModuleList.push({ id: 'INCIDENT', label: 'Incident' });
    this.permissions = this.loggedInUserService.getModulePermissions();
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();

  }


  get f() { return this.angFormAssign.controls; }

  hasModulePermission(moduleName: string, permissions: any[], additionalModule: string[]): boolean {
    for (let k = 0; k < permissions.length; k++) {
      if (additionalModule.length > 0) {
        if (additionalModule.includes(permissions[k].moduleName) && permissions[k].actions.includes('READ')) {
          return true
        }
      } else {
        if (permissions[k].moduleName === moduleName && permissions[k].actions.includes('READ')) {
          return true;
        }
      }
    }
    return false;
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

  startChat(chatRoomId) {
    alert(chatRoomId);
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

  downloadFile(attachentId: number) {
    this.mediaService
      .downloadMediaService(attachentId, {})
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: data.type });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          //alert('Please disable your Pop-up blocker and try again.');
        }
      });
    return false;
  }

  getFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angChatForm.controls['fileInput'];
      if (file.errors && !file.errors.validAttachments && !file.errors.mustBeLessThan2MB) {
        return;
      }
      let validMimeTpes = ["application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "application/x-msexcel", "application/x-excel", "application/vnd.ms-excel", "application/excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingm", "image/jpeg", "image/pjpeg", "image/png"];
      if (!validMimeTpes.includes(type)) {
        file.setErrors({ validAttachments: true, mustBeLessThan2MB: false });
      } else if (size > (3145728)) {
        file.setErrors({ validAttachments: false, mustBeLessThan2MB: true });
      } else {
        file.setErrors(null);
        this.selectedFiles = event.target.files;
      }
      // console.log('Name: ' + name + "\n" +
      //   'Type: ' + type + "\n" +
      //   'Last-Modified-Date: ' + modifiedDate + "\n" +
      //   'Size: ' + Math.round(size / 1024) + " KB");
      if (typeof this.selectedFiles != "undefined") {
        if (this.selectedFiles.length > 0) {
          const formData: FormData = new FormData();
          for (var i = 0; i < this.selectedFiles.length; i++) {
            formData.append("file", this.selectedFiles[i]);
          }
          let headers = new Headers();
          headers.append('Content-Type', 'multipart/form-data');
          headers.set('Accept', 'application/json');
          this.mediaService
            .saveMediaService(0, EntityType.CHAT, MediaType.CHAT_COMMUNICATION, this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY), formData, { 'headers': headers })
            .subscribe((data: any) => {
              if (data.length > 0) {
                let content = '<span class="actionable-span attachment" data-cntrl="' + data[0].id + '">' + data[0].fileName + '&nbsp;&nbsp;</span>';
                var chatMessage = new ChatMessage();
                chatMessage.content = content;
                chatMessage.roomId = this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY);
                chatMessage.senderId = this.loggedInUser.userId;
                //console.log(chatObj);
                this.miscService.startChatService(chatMessage)
                  .subscribe((data: any) => {
                    //console.log("chat synched!")
                    //console.log("completed....", JSON.stringify(data));
                    //this.bsModalRef.hide();
                    //this.chatRoomService.setChatRoomId(data.roomId);
                    //this.angChatForm.reset();
                  });
              }
            });
        }
      }
    }
  }


  closeChat(needConfirmation: boolean = true) {
    if (needConfirmation) {
      if (this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY) != null) {
        let res = window.confirm("Do you really want to close the chat session?")
        if (res) {
          this.service.closeChatService(this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY), {})
            .subscribe((data: any) => {
              this.chatWSSocketService.disconnect();
              this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY);
              this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_ROOM_MSG_KEY_PREFIX);
              this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_START_KEY);
              this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_END_KEY);
              this.clientStorageService.remove(AppUtility.APP_HAS_ACTIVE_CHAT_SESSION);
              //alert("data");
              $(function () {
                $('#live-chat').addClass('d-none');
              });
            });
          this.chatSessionActive = false;
          this.chatSelection = false;
        }
      } else {
        this.chatSessionActive = false;
        this.chatSelection = false;
        $(function () {
          $('#live-chat').addClass('d-none');
        });
      }
    } else {
      this.chatSessionActive = false;
      this.chatSelection = false;
      if (this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY) != null) {
        this.service.closeChatService(this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY), {})
          .subscribe((data: any) => {
            this.chatWSSocketService.disconnect();
            this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY);
            this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_ROOM_MSG_KEY_PREFIX);
            this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_START_KEY);
            this.clientStorageService.remove(AppUtility.APP_LAST_CHAT_END_KEY);
            this.clientStorageService.remove(AppUtility.APP_HAS_ACTIVE_CHAT_SESSION);
            //alert("data");
            $(function () {
              $('#live-chat').addClass('d-none');
            });
          });
      } else {
        $(function () {
          $('#live-chat').addClass('d-none');
        });
      }
    }
  }

  // chatInteractation(content: string) {
  //   var chatObj = {
  //     "content": content,
  //     "roomId": this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY),
  //     "senderId": this.loggedInUser.userId,
  //   };
  //   console.log(chatObj);
  // }

  chatInteractation() {
    if (this.angChatForm.valid) {
      //console.log(this.angForm);
      var chatMessage = new ChatMessage();
      chatMessage.content = this.angChatForm.controls['chatMessage'].value;
      chatMessage.roomId = this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY);
      chatMessage.senderId = this.loggedInUser.userId;
      //console.log(chatObj);
      this.miscService.startChatService(chatMessage)
        .subscribe((data: any) => {
          //console.log("completed....", JSON.stringify(data));
          //this.bsModalRef.hide();
          //this.chatRoomService.setChatRoomId(data.roomId);
          this.angChatForm.reset();
        });
    } else {
      console.log("Invalid Form!");
    }
  }

  checkChat() {
    if (!isNaN(this.getLastChatAction())) {
      const chatNow = Date.now();
      const chatEndTimeleft = this.getLastChatAction() + AppUtility.APP_CHAT_SESSION_END_INTERVAL * 60 * 1000;
      const warningTimeleft = this.getLastAction() + AppUtility.APP_CHAT_SESSION_WARNING_INTERVAL * 60 * 1000;
      const chatEndDiff = chatEndTimeleft - chatNow;
      const isTimeout = chatEndDiff < 0;
      var tempTime = moment.duration(chatEndDiff);
      this.chatTimeLeft = tempTime.minutes().toString().padStart(2, "0") + ":" + tempTime.seconds().toString().padStart(2, "0");
      //console.log("chatEndTimeleft => " + chatEndTimeleft);
      //console.log("warningTimeleft => " + warningTimeleft);
      //TODO: need to handle setinterval
      //console.log("diff => " + diff);
      //var tempTime = moment.duration(diff);
      //var timeLeft = tempTime.hours() + ":" + tempTime.minutes() + ":" + tempTime.seconds();
      //console.log(timeLeft);
      //console.log("getModalsCount -", " ", diff, this.modalService.getModalsCount());
      if (!isTimeout && warningTimeleft - chatNow < 0) {
        if (this.clientStorageService.get(AppUtility.APP_CHAT_SESSION_ALERT_ENABLED_KEY) == "0") {
          $(function () {
            $('#chat-session-timeout').css({ "backgroundColor": "red", "color": "white" });
          });
          this.clientStorageService.set(AppUtility.APP_CHAT_SESSION_ALERT_ENABLED_KEY, "1");
          //console.log("chat session going to end!");
        }
      } else if (isTimeout) {
        if (this.clientStorageService.get(AppUtility.APP_CHAT_SESSION_ALERT_ENABLED_KEY) == "1") {
          clearInterval(this.timerHandler);
          //console.log("chat session ended!");
          //this.closeChat();
          $(function () {
            $('.chat-close').trigger('click');
          });
        }
      }
    }
  }

  chatInitInterval() {
    this.chatTimerHandler = setInterval(() => {
      this.checkChat();
    }, AppUtility.APP_CHAT_ACTIVITY_CHECK_INTERVAL);
  }

  public getLastChatAction() {
    return parseInt(this.clientStorageService.get(AppUtility.APP_LAST_CHAT_START_KEY));
  }

  public setStartChatAction(lastAction: number) {
    this.clientStorageService.set(AppUtility.APP_LAST_CHAT_START_KEY, lastAction.toString());
  }

  chatStart() {
    this.clientStorageService.set(AppUtility.APP_CHAT_SESSION_ALERT_ENABLED_KEY, "0");
    this.setStartChatAction(Date.now());
    this.chatInitInterval();
    this.clientStorageService.set(AppUtility.APP_HAS_ACTIVE_CHAT_SESSION, "1");
    //console.log("start chat");
  }

  isAgent(senderId: number) {
    return senderId != this.chatInitiator;
  }

  ngOnInit(): void {
    this.connect();
    //console.log(this.clientStorageService.get(AppUtility.APP_LOGGEDIN_USR_ROLES));
    var roles = this.clientStorageService.get(AppUtility.APP_LOGGEDIN_USR_ROLES);
    var self = this;
    $(function () {
      if (!roles.includes('EMP') && roles.includes('AGENT')) {
        $('.nav-item').removeClass('highlighted-yellow');
      }
      // $('#live-chat header').on('click', function (e) {
      //   if (!$(e.target).hasClass('chat-close')) {
      //     alert("sdsadsad");
      //     $('.chat').slideToggle(300, 'swing');
      //     $('.chat-message-counter').fadeToggle(300, 'swing');
      //   }
      // });
      // $('.chat-close').on('click', function (e) {
      //   e.preventDefault();
      //   //$('#live-chat').fadeOut(300);
      //   self.closeChat();
      // });
      $(document).delegate('.attachment', 'click', function () {
        self.downloadFile($(this).attr('data-cntrl'));
        //self.closeModal();
        //$('#live-chat').removeClass("d-none");
        //$('#live-chat').fadeIn(300);
        //self.initateChat($('#accept').attr("data-com-id"));
      });
      // $('#chatMessage').keypress(function (event) {
      //   var keycode = (event.keyCode ? event.keyCode : event.which);
      //   if (keycode == '13') {
      //     var chatMessage = $(this).val();
      //     //alert(chatMessage);
      //     $(this).val("");
      //     //call 
      //     self.chatInteractation(chatMessage);
      //   }
      // });
    });
    this.subscription = this.chatRoomService.currentChatRoomId.subscribe(chatRoomId => {
      if (chatRoomId !== null) {
        this.chatRoomId = chatRoomId;
        this.clientStorageService.set(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY, chatRoomId);
        this.chatSelection = false;
        this.chatSessionActive = true;
        this.chatStart();
        //console.log(this.chatRoomId);
        this.chatWSSocketService.connect(this.chatRoomId);
        // subscribe receives the value.
        this.chatService.chatMessage
          .subscribe((data: any) => {
            if (data && Object.keys(data).length === 0 && Object.getPrototypeOf(data) === Object.prototype) {
              this.closeChat(false);
              return;
            }
            if (data.recipientId == null) {
              this.chatInitiator = data.senderId;
            }
            //console.log('receive message', data);
            //console.log("here I am");
            $(function () {
              if ($('#live-chat').hasClass('d-none')) {
                $('#live-chat').removeClass('d-none');
              }
            });
            if (data.recipientId != null) {
              this.canCommunate = true;
            } else {
              this.canCommunate = false;
            }
            //console.log(this.canCommunate);
            var msgIdsKey = AppUtility.APP_LAST_CHAT_ROOM_MSG_KEY_PREFIX + "-" + this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY) + "-" + this.loggedInUser.userId;
            var chatIds = [];
            //console.log(this.clientStorageService.get(msgIdsKey));
            if (this.clientStorageService.get(msgIdsKey) == null) {
              chatIds.push(data.id);
              this.clientStorageService.set(msgIdsKey, JSON.stringify(chatIds));
            } else {
              chatIds = JSON.parse(this.clientStorageService.get(msgIdsKey));
              if (chatIds.includes(data.id)) {
                return;
              }
              chatIds.push(data.id);
              this.clientStorageService.set(msgIdsKey, JSON.stringify(chatIds));
            }
            $(function () {
              let chatClass = data.senderType == 'AGENT' ? 'chat-feedback' : '';
              //console.log(chatClass);
              $('#current-chat-window').append("<div class=\"chat-message " + chatClass + " clearfix\">" +
                "          <img src=\"assets/img/avatars/user.jpg\" class=\"img-avatar\" width=\"32\" height=\"32\" alt=\"admin@bootstrapmaster.com\" />" +
                "          <div class=\"chat-message-content clearfix\">" +
                "            <div class=\"chat-message-color\">" +
                "              <span class=\"chat-time\">" + moment().format("HH:mm") + "</span>" +
                "              <h5>" + data.senderName + "</h5>" +
                "              <p>" + data.content + "</p>" +
                "            </div>" +
                "          </div>" +
                "        </div>");
              $('#current-chat-window').scrollTop($('#current-chat-window')[0].scrollHeight);
            });

            // let n = new Notification();
            // let u = new User();
            // n.id = data.id;
            // n.notificationTitle = data.title;
            // n.notificationBody = data.body;
            // n.status = data.status;
            // n.notificationType = data.notificationType;
            // u.id = data.userId;
            // n.user = u;
            // this.notifications.push(n);
            // this.notifications.sort(function (a, b) {
            //   if (b.id > a.id) return 1;
            //   if (a.id > b.id) return -1;
            //   return 0;
            // });
            // this.unreadNotifications = this.notifications.length;
          });
      }
    });

    if (
      this.clientStorageService.get(AppUtility.APP_HAS_ACTIVE_CHAT_SESSION) == "1"
      &&
      this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY) != null
    ) {
      if (!isNaN(this.getLastChatAction())) {
        const chatNow = Date.now();
        const chatEndTimeleft = this.getLastChatAction() + AppUtility.APP_CHAT_SESSION_END_INTERVAL * 60 * 1000;
        const warningTimeleft = this.getLastAction() + AppUtility.APP_CHAT_SESSION_WARNING_INTERVAL * 60 * 1000;
        const chatEndDiff = chatEndTimeleft - chatNow;
        const isTimeout = chatEndDiff < 0;
        //console.log("chatEndTimeleft => " + chatEndTimeleft);
        //console.log("warningTimeleft => " + warningTimeleft);
        //TODO: need to handle setinterval
        //console.log("diff => " + diff);
        //var tempTime = moment.duration(diff);
        //var timeLeft = tempTime.hours() + ":" + tempTime.minutes() + ":" + tempTime.seconds();
        //console.log(timeLeft);
        //console.log("getModalsCount -", " ", diff, this.modalService.getModalsCount());
        if (!isTimeout) {
          this.miscService.chatMassagesService(this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY))
            .subscribe((data: any) => {
              //console.log("completed....", JSON.stringify(data));
              var self = this;
              $(function () {
                $('#live-chat').removeClass('d-none');
                let user = self.getLoggedInUser();
                //console.log(user);
                for (let k = 0; k < data.length; k++) {
                  let chatClass = data[k].senderType == 'AGENT' ? 'chat-feedback' : '';
                  $('#current-chat-window').append("<div class=\"chat-message " + chatClass + " clearfix\">" +
                    "          <img src=\"assets/img/avatars/user.jpg\" class=\"img-avatar\" width=\"32\" height=\"32\" alt=\"admin@bootstrapmaster.com\" />" +
                    "          <div class=\"chat-message-content clearfix\">" +
                    "            <div class=\"chat-message-color\">" +
                    "              <span class=\"chat-time\">" + moment.utc(data[k].conversationTime).tz(user.timeZone).format("HH:mm") + "</span>" +
                    "              <h5>" + data[k].senderName + "</h5>" +
                    "              <p>" + data[k].content + "</p>" +
                    "            </div>" +
                    "          </div>" +
                    "        </div>");
                  $('#current-chat-window').scrollTop($('#current-chat-window')[0].scrollHeight);
                }
              });
              this.chatRoomService.setChatRoomId(this.clientStorageService.get(AppUtility.APP_LAST_CHAT_ROOM_ID_KEY));
            });
        }
      }
    }

  }

  getLoggedInUser() {
    return this.loggedInUser;
  }

  ngAfterViewInit() {
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
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

  closeChatInitiateForm() {
    alert("Close Chat Initiate Form!");
  }

  selectAgent() {
    if (this.chatSessionActive) {
      this.chatSelection = false;
      this.chatSessionActive = true;
    } else {
      this.chatSelection = true;
      this.chatSessionActive = false;
      $(function () {
        $('#live-chat').removeClass('d-none');
      });
    }
  }

  chatAction(action: string) {
    alert(action);
  }

  @ViewChild("parentModule") parentModule;
  populateModule(parentModule) {
    if (typeof parentModule !== 'undefined') {
      let c = 0;
      this.moduleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == null && this.permissions[i].licenseType == parentModule.id) {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
          this.moduleList[c] = p;
          c++;
        }
      }
      this.angFormAssign.controls['moduleId'].setValue(null);
      this.angFormAssign.controls['subModuleId'].setValue(null);
    } else {
      this.angFormAssign.controls['moduleId'].setValue(null);
      this.angFormAssign.controls['subModuleId'].setValue(null);
    }
  }

  @ViewChild("moduleId") moduleId;
  populateSubmodule(moduleId) {
    if (typeof moduleId !== 'undefined') {
      let c = 0;
      this.subModuleList = [];
      let p;
      for (let i = 0; i < this.permissions.length; i++) {
        if (this.permissions[i].appModule == false && this.permissions[i].moduleParentId == moduleId.moduleId && this.permissions[i].licenseType == this.angFormAssign.controls['parentModule'].value) {
          p = new Permission(this.permissions[i]);
          p.customerModuleName = p.customerModuleName;
          //AppUtility.toTitleCase(p.customerModuleName);
          this.subModuleList[c] = p;
          c++;
        }
      }
      this.angFormAssign.controls['subModuleId'].setValue(null);
    } else {
      this.angFormAssign.controls['subModuleId'].setValue(null);
    }
  }

  initiateChat(req: any) {
    this.miscService.startChatService(req)
      .subscribe(
        (data: any) => {
          //console.log("completed....", JSON.stringify(data));
          this.chatRoomService.setChatRoomId(data.roomId);
        },
        // (error) => {
        //   console.log("here I am"+error);
        //   return false;
        // }
      );
  }

  chatInitiate() {
    if (this.angFormAssign.valid) {
      var chatmessage = new ChatMessage();
      chatmessage.content = this.angFormAssign.controls['message'].value;
      chatmessage.moduleId = this.angFormAssign.controls['moduleId'].value;
      chatmessage.subModuleId = this.angFormAssign.controls['subModuleId'].value;
      chatmessage.attachments = [];
      chatmessage.recipientId = null;
      chatmessage.recipientName = null;
      chatmessage.senderId = this.loggedInUser.userId;
      chatmessage.senderName = null;
      chatmessage.status = null;
      chatmessage.intiateChat = true;
      this.initiateChat(chatmessage);
      this.angFormAssign.reset();
    } else {
      console.log("Invalid Form!");
    }
  }

}