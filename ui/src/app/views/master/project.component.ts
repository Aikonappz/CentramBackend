import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { tap } from 'rxjs/operators';
import { DistributionList } from '../../model/DistributionList';
import { Status } from '../../model/enumerator/Status';
import { Vendor } from '../../model/Vendor';
import { ProjectDataSource } from '../../service/datasource/ProjectDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { MiscService } from '../../service/MiscService';
import { LoggedInUser } from '../../model/LoggedInUser';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit {
  moduleName: string = "PROJECT_MASTER";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['project', 'module', 'subModule', 'status', 'action'];
  datasource: ProjectDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  type: string;
  hasAllocationType: boolean = true;
  loggedinUser: LoggedInUser;

  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private route: ActivatedRoute,
    private service: MiscService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedinUser = loggedInUserService.getLoggedInUser();
  }

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
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
    this.datasource = new ProjectDataSource(this.service);
    this.datasource.loadData(0, 10, this.loggedinUser.licenseType, {});
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
        tap(() => this.loadData({ vendorType: this.type.toUpperCase() }))
      )
      .subscribe();
  }

  edit(dl: DistributionList) {
    this.router.navigate(['/master/project/edit/' + dl.id]);
  }

  add() {
    this.router.navigate(['/master/project/add']);
  }

  updateStatus(vendor: Vendor) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + vendor.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updatePrioritiesStatusService([vendor.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  loadData(req: any = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, this.loggedinUser.licenseType, req);
  }

  formateManager(str: String[]) {
    // var strArray = 
    // strArray = strArray.filter((item) => {
    //   return item !== '';
    // });
    //console.log(strArray.join(",").split(",,").join(","));
    return str.join('<br/>');
  }

}
