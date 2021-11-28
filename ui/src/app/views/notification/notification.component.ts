import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
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
  displayedColumns = ['notificationTitle', 'notificationBody', 'notificationType', 'status', 'action'];
  private datasource: NotificationDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private service: MiscService,
    private titleService: Title,
    private router: Router,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
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
}
