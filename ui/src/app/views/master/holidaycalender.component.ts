import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Status } from '../../model/enumerator/Status';
import { HolidayCalender } from '../../model/HolidayCalender';
import { LocationVO } from '../../model/LocationVO';
import { Priority } from '../../model/Priority';
import { HolidayCalenderDataSource } from '../../service/datasource/HolidayCalenderDataSource';
import { LocationDataSource } from '../../service/datasource/LocationDataSource';
import { PriorityDataSource } from '../../service/datasource/PriorityDataSource';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-holidaycalender',
  templateUrl: './holidaycalender.component.html',
  styleUrls: ['./holidaycalender.component.scss']
})
export class HolidayCalenderComponent implements OnInit {
  displayedColumns = ['year', 'action'];
  private datasource: HolidayCalenderDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  constructor(
    private titleService: Title,
    private router: Router,
    private service: MiscService
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
    this.datasource = new HolidayCalenderDataSource(this.service);
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

  edit(loc: LocationVO) {
    this.router.navigate(['/master/calender/edit/' + loc.id]);
  }
  add() {
    this.router.navigate(['/master/calender/add']);
  }

  updateStatus(hc: HolidayCalender) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + hc.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updatePrioritiesStatusService([hc.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  loadData() {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize);
  }

  download(obj: HolidayCalender) {
    this.service
      .downloadholidayCalenderService(obj.id, {})
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: "text/csv" });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          //alert('Please disable your Pop-up blocker and try again.');
        }
      });
  }

}
