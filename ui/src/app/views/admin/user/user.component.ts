import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UserDataSource } from '../../../service/datasource/UserDataSource';
import { MatPaginator } from '@angular/material/paginator';
import { tap } from 'rxjs/operators';
import { UserService } from '../../../service/UserService';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { UserVO } from '../../../model/UserVO';
import { Status } from '../../../model/enumerator/Status';
import { User } from '../../../model/User';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { LoggedInUser } from '../../../model/LoggedInUser';
import { MiscService } from '../../../service/MiscService';
import { ViewUserDetail } from './modal/ViewUserDetailModal';
import { UploadUserComponent } from './modal/UploadUserModal';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit, OnDestroy {
  moduleName: string = "USER";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  modalRef: BsModalRef;
  displayedColumns = ['name', 'email', 'contact', 'status', 'action'];
  private datasource: UserDataSource
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  usr: User;
  defaultStatus: any = 'ALL';
  statusFlag: string = 'ALL';
  drStatus: { isOpen: boolean } = { isOpen: false };
  disabled: boolean = false;
  isDropup: boolean = true;
  autoClose: boolean = false;
  roles: string[];
  loggedInUser: LoggedInUser;
  vendorList: any[];
  c: number = 0;
  searchedData: Object = {};
  filterTypes: any[] = [];
  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private miscService: MiscService,
    private modalService: BsModalService,
  ) {
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
      vendorId: new FormControl(null, [
      ]),
      // userType: new FormControl(null, [
      // ]),
    });
    this.usr = new User();
    this.usr.status = this.defaultStatus;
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.roles = this.loggedInUser.roles;
    if (!this.roles.includes('APP_ADMIN')) {
      this.miscService
        .vendorsService()
        .subscribe((data: any) => {
          let vendors = data.content;
          this.c = 0;
          this.vendorList = [];
          for (let indx = 0; indx < vendors.length; indx++) {
            if (String(vendors[indx].status) == 'ACTIVE') {
              this.vendorList[this.c++] = vendors[indx];
              //Object.assign({ "id": vendors[indx].id, "version": vendors[indx].version, "name": vendors[indx].name });
            }
          }
        });
      this.filterTypes = [{ "id": '', "label": "ALL" }, { "id": 'USER', "label": "Employee" }, { "id": 'AGENT', label: "Agent" }];
    }
  }

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
  }

  onHidden(): void {
    //console.log('Dropdown is hidden');
  }
  onShown(): void {
    //console.log('Dropdown is shown');
  }
  isOpenChange(): void {
    //console.log('Dropdown state is changed');
  }

  toggleDropdown($event: MouseEvent): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.drStatus.isOpen = !this.drStatus.isOpen;
  }

  change(value: boolean): void {
    this.drStatus.isOpen = value;
  }

  ngOnDestroy() {
    this.drStatus.isOpen = false;
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
    this.datasource = new UserDataSource(this.userService);
    this.datasource.loadUserVOs();
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

  loadData(req = {}) {
    if (this.searchedData.hasOwnProperty('email')) {
      req = this.searchedData;
    }
    this.datasource.loadUserVOs(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  loadPage() {
    this.angForm.reset();
    this.searchedData = {};
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
    this.router.navigate(['/admin/user/edit/' + user.id]);
  }

  add() {
    this.router.navigate(['/admin/user/add']);
  }

  upload() {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      initialState: {
      }
    };
    this.modalRef = this.modalService.show(UploadUserComponent, config);
    this.modalRef.content.closeBtnName = 'Close';
  }

  download() {
    this.userService
      .downloadUsersService({})
      .subscribe((data: any) => {
        //console.log(data);
        let blob = new Blob([data], { type: "text/csv" });
        let url = window.URL.createObjectURL(blob);
        let pwa = window.open(url);
        if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
          alert('Please disable your Pop-up blocker and try again.');
        }
      });
  }

  get f() { return this.angForm.controls; }

  @ViewChild("status") status;
  onChange(inp: string) {
    this.statusFlag = inp;
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      let vendorId = this.angForm.controls['vendorId'].value;
      //let userType = this.angForm.controls['userType'].value;
      this.usr.email = this.angForm.controls['userEmail'].value;
      this.usr.employeeId = this.angForm.controls['userEmployeeId'].value;
      this.usr.status = this.statusFlag;
      this.searchedData = {
        "email": this.usr.email == null ? '' : this.usr.email,
        "employeeId": this.usr.employeeId == null ? '' : this.usr.employeeId,
        "status": this.usr.status,
        "vendorId": vendorId == null ? '' : vendorId,
      };
      this.loadData(this.searchedData);
      //console.log(JSON.stringify(this.org));
    } else {
      console.log("Invalid Form!");
    }
  }

  view(element: UserVO) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      usr: element
    };
    this.modalRef = this.modalService.show(ViewUserDetail,
      Object.assign({}, config, { initialState })
    );
  }

}