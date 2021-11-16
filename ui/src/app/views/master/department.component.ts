import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Department } from '../../model/Department';
import { Status } from '../../model/enumerator/Status';
import { DepartmentDataSource } from '../../service/datasource/DepartmentDataSource';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-department',
  templateUrl: './department.component.html',
  styleUrls: ['./department.component.scss']
})
export class DepartmentComponent implements OnInit {
  displayedColumns = ['name', 'status', 'action'];
  private datasource: DepartmentDataSource;
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
    this.datasource = new DepartmentDataSource(this.service);
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

  edit(org: Department) {
    this.router.navigate(['/masters/department/edit/' + org.id]);
  }
  add() {
    this.router.navigate(['/masters/department/add']);
  }

  updateStatus(org: Department) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + org.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updateDepartmentsStatusService([org.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  loadData() {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize);
  }

}