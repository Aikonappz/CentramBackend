import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { Incident } from '../../model/Incident';
import { IncidentDataSource } from '../../service/datasource/IncidentDataSource';
import { IncidentService } from '../../service/IncidentService';

@Component({
  selector: 'app-incident',
  templateUrl: './incident.component.html',
  styleUrls: ['./incident.component.scss']
})
export class IncidentComponent implements OnInit {
  displayedColumns = ['title', 'priority', 'watchList', 'raisedAt', 'slaAt', 'status', 'action'];
  private datasource: IncidentDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private titleService: Title,
    private router: Router,
    private service: IncidentService
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
    this.datasource = new IncidentDataSource(this.service);
    this.datasource.loadData();
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
        tap(() => this.loadData())
      )
      .subscribe();
  }

  edit(inc: Incident) {
    this.router.navigate(['/incident/edit/' + inc.id]);
  }
  add() {
    this.router.navigate(['/incident/raise']);
  }

  loadData() {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize);
  }
  formatDateTime(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_TIME_FORMAT);
    }
    return null;
  }
}
