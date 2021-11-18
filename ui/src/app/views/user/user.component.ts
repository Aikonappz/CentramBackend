import { Component, OnInit, ViewChild } from '@angular/core';
import { UserDataSource } from '../../service/datasource/UserDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import { UserService } from '../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { UserVO } from '../../model/UserVO';
import { Status } from '../../model/enumerator/Status';
import { User } from '../../model/User';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  displayedColumns = ['name', 'contact', 'employeeId', 'location', 'department', 'projectCode', 'status', 'action'];
  private userVODatasource: UserDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  usr: User;
  defaultStatus: any = 'ALL';
  statusFlag: boolean = true;
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private userService: UserService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      userEmail: new FormControl('', [
        Validators.maxLength(255)
      ]),
      userEmployeeId: new FormControl('', [
        Validators.maxLength(255)
      ]),
      status: new FormControl('ALL', [

      ]),
    });
    this.usr = new User();
    this.usr.status = this.defaultStatus;
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
    this.userVODatasource = new UserDataSource(this.userService);
    this.userVODatasource.loadUserVOs();
  }

  ngAfterViewInit() {
    this.userVODatasource.counter$
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

  loadData(req = {}) {
    this.userVODatasource.loadUserVOs(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  loadPage() {
    this.angForm.reset();
    this.loadData({});
    this.usr.status = this.defaultStatus;
  }

  updateStatus(user: UserVO) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + user.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.userService
        .updateStatusService([user.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }

  edit(user: UserVO) {
    this.router.navigate(['/user/edit/' + user.id]);
  }

  add() {
    this.router.navigate(['/user/add']);
  }

  upload() {
    window.alert("upload");
  }

  download() {
    window.alert("download");
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.usr.email = this.angForm.controls['userEmail'].value;
      this.usr.employeeId = this.angForm.controls['userEmployeeId'].value;
      this.loadData({
        "email": this.usr.email == null ? '' : this.usr.email,
        "employeeId": this.usr.employeeId == null ? '' : this.usr.employeeId,
        "status": this.usr.status
      });
      //console.log(JSON.stringify(this.org));
    } else {
      console.log("Invalid Form!");
    }
  }

  @ViewChild("status") status;
  onChange(inp: string) {
    let val: any = inp;
    this.usr.status = val;
  }

}