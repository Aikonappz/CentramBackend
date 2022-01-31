import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { Notification } from '../../model/Notification';
import { NotificationDataSource } from '../../service/datasource/NotificationSource';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {
  notification: Notification;
  modalRef: BsModalRef;
  displayedColumns = ['notificationTitle', 'notificationBody', 'action'];
  private datasource: NotificationDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private service: MiscService,
    private titleService: Title,
    private router: Router,
    private modalService: BsModalService,
    private route: ActivatedRoute,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    //console.log("test...");
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
    this.datasource.load(0, 10, { status: "ALL" });
    if (!this.route.snapshot.paramMap.has('id')) {

    } else {
      this.route.params.subscribe(params => {
        let entityId = Number(this.route.snapshot.paramMap.get('id'));
        this.viewNotification(entityId);
      });
    }
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
    this.datasource.load(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  view(id: number) {
    this.router.navigate(['/notification/view/' + id]);
  }

  viewNotification(id: number) {
    this.service.notificationService(id, {})
      .subscribe((data: Notification) => {
        //console.log("completed....");
        // const initialState = {
        //   notification: data,
        // };
        // this.modalRef = this.modalService.show(NotificationViewComponent, { initialState });
        // this.modalRef.content.closeBtnName = 'Close';

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
        this.modalRef = this.modalService.show(NotificationViewComponent,
          Object.assign({}, config, { initialState })
        );



      });
  }
}
@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-info-circle"></i>&nbsp; &nbsp;{{notification.notificationTitle}}</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
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
                          <div [innerHTML]="notification.notificationBody"></div>
                      </div>
                  </div>
              </div>
          </div>
      </div>
  </div>
</div>`
})
export class NotificationViewComponent implements OnInit {
  notification: Notification;
  constructor(public bsModalRef: BsModalRef,) { }
  ngOnInit() { }
  ngAfterViewInit() { }
  ngAfterContentInit() { }
}