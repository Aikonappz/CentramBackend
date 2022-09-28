import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Status } from '../../model/enumerator/Status';
import { Notification, NotificationList } from '../../model/Notification';
import { ChatRoomService } from '../../service/ChatRoomService';
import { NotificationDataSource } from '../../service/datasource/NotificationSource';
import { MiscService } from '../../service/MiscService';
import { NotificationViewComponent } from './modal/NotificationViewComponent';
declare var $: any;

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {
  notification: Notification;
  modalRef: BsModalRef;
  displayedColumns = ['notificationTitle', 'action'];
  private datasource: NotificationDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  entityId: number;
  angForm: FormGroup;
  searchedData: Object = {};
  subscription: Subscription;
  chatRoomId: string;;

  constructor(
    private fb: FormBuilder,
    private service: MiscService,
    private titleService: Title,
    private router: Router,
    private modalService: BsModalService,
    private route: ActivatedRoute,
    private chatRoomService: ChatRoomService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      searchValue: new FormControl('', [
      ]),
    });
    this.service.notificationsService({ status: "PUSHED", }).subscribe((data: NotificationList) => {
      if (typeof data.content != "undefined" && data.content.length > 0) {
        let updatedNotifications = [];
        for (let k in data.content) {
          updatedNotifications.push(data.content[k].id);
        }
        this.service
          .updateNotificationsStatusService(updatedNotifications, Status.PULLED)
          .subscribe((data: any) => {
          });
      }
    });
  }

  loadPage() {
    this.angForm.reset();
    this.searchedData = {};
    this.loadData({});
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
    this.datasource = new NotificationDataSource(this.service);
    if (!this.route.snapshot.paramMap.has('id')) {
      this.datasource.load(0, 10, { status: "ALL" });
    } else {
      this.route.params.subscribe(params => {
        this.entityId = Number(this.route.snapshot.paramMap.get('id'));
        this.viewNotification(this.entityId)
          .then(() => {
            this.datasource.load(0, 10, { status: "ALL" })
          });
      });
    }
    var self = this;
    $(function () {
      $(document).delegate('#accept', 'click', function () {
        self.closeModal();
        $('#live-chat').removeClass("d-none");
        $('#live-chat').fadeIn(300);
        self.initateChat($('#accept').attr("data-com-id"));
      });
      $(document).delegate('#reject', 'click', function () {
        self.closeModal();
      });
    });
  }

  initateChat(chatReqId) {
    this.service.initiateChatService(chatReqId, {});
    this.chatRoomService.setChatRoomId(chatReqId);
  }

  closeModal() {
    console.log("clossing modal....");
    this.modalRef.hide();
  }


  ngAfterViewInit() {
    this.datasource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();

    this.paginator.page
      .pipe(
        tap(() => this.loadData({ status: "ALL" }))
      )
      .subscribe();
  }

  loadData(req = {}) {
    if (this.searchedData.hasOwnProperty('searchValue')) {
      req = this.searchedData;
    }
    this.datasource.load(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  view(id: number) {
    this.router.navigate(['/notification/view/' + id]);
  }

  formSubmit() {
    if (this.angForm.valid) {
      let saerchVal = this.angForm.controls['searchValue'].value;
      this.searchedData = {
        "searchValue": saerchVal == null ? '' : saerchVal,
      };
      this.loadData(this.searchedData);
    } else {
      console.log("Invalid Form!");
    }
  }

  viewNotification(id: number) {
    this.service.notificationService(id, {})
      .subscribe((data: Notification) => {
        // let notificationBody = data.notificationBody;
        // try {
        //   data.notificationBody = window.atob(notificationBody);
        //   console.log("Encoded -> ", data.notificationBody);
        // } catch (e) {
        //   data.notificationBody = notificationBody;
        //   console.log("Not Encoded -> ", data.notificationBody);
        // }
        //console.log(data.notificationBody);
        const config: ModalOptions = {
          backdrop: 'static',
          keyboard: false,
          animated: true,
          ignoreBackdropClick: true,
          class: data.notificationBody.length > 100 ? 'modal-bg' : 'modal-bg',
        };
        const initialState = {
          notification: data,
        };
        this.modalRef = this.modalService.show(NotificationViewComponent, Object.assign({}, config, { initialState }));
      });
    return Promise.resolve("Success");
  }
}