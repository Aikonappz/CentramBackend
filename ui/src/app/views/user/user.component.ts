import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
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
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { MiscService } from '../../service/MiscService';

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
      vendorId: new FormControl('', [
      ]),
      userType: new FormControl("ALL", [
      ]),
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
    }
  }

  hasPermission(action: string): boolean {
    return this.loggedInUserService.hasPermissionByName(this.moduleName, action);
  }

  onHidden(): void {
    console.log('Dropdown is hidden');
  }
  onShown(): void {
    console.log('Dropdown is shown');
  }
  isOpenChange(): void {
    console.log('Dropdown state is changed');
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
    this.datasource.loadUserVOs(this.paginator.pageIndex, this.paginator.pageSize, req);
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
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      initialState: {
      }
    };
    this.modalRef = this.modalService.show(UserUploadComponent, config);
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
      let userType = this.angForm.controls['userType'].value;
      this.usr.email = this.angForm.controls['userEmail'].value;
      this.usr.employeeId = this.angForm.controls['userEmployeeId'].value;
      this.usr.status = this.statusFlag;
      this.loadData({
        "email": this.usr.email == null ? '' : this.usr.email,
        "employeeId": this.usr.employeeId == null ? '' : this.usr.employeeId,
        "status": this.usr.status,
        "filterType": userType,
        "vendorId": vendorId,
      });
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

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> View User Details</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
      <div class="col-sm-12">
          <div class="card ">
              <table class="table table-bordered">
                  <tr>
                      <td>Name</td>
                      <td>{{usr.firstName}} {{usr.lastName}}</td>
                  </tr>
                  <tr>
                      <td>Email</td>
                      <td>{{usr.email}}</td>
                  </tr>
                  <tr>
                      <td>Contact</td>
                      <td>
                      <b>{{usr.contactNo}}</b><br/>
                         {{usr.secContactNo}}
                      </td>
                  </tr>
                  <tr>
                      <td>Employee Id.</td>
                      <td>{{usr.employeeId}}</td>
                  </tr>
                  <tr>
                      <td>Project Code</td>
                      <td>{{usr.projectCode}}</td>
                  </tr>
                  <tr>
                      <td>Roles</td>
                      <td>{{usr.roleNames.join(',')}}</td>
                  </tr>
                  <tr>
                      <td>Location</td>
                      <td>{{usr.location}}</td>
                  </tr>
                  <tr>
                      <td>Department</td>
                      <td>{{usr.department}}</td>
                  </tr>
                  <tr>
                      <td>Organization</td>
                      <td>{{usr.organisation}}</td>
                  </tr>
                  <tr>
                      <td>Status</td>
                      <td>{{usr.status}}</td>
                  </tr>
              </table>
          </div>
      </div>
  </div>
</div>`
})
export class ViewUserDetail implements OnInit {
  usr: UserVO;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: UserService,
    public options: ModalOptions,
  ) {
  }
  ngOnInit() {
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }
}

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="fa fa-upload"></i> Upload Bulk User</h6>
  <button type="button" class="close pull-right" aria-label="Close" (click)="bsModalRef.hide()">
      <span aria-hidden="true">&times;</span>
  </button>
</div>
<div class="modal-body">
  <div class="row">
    <div class="col-sm-12">
      <div class="card ">          
          <form [formGroup]="angFormUpload" (ngSubmit)="uploadFile()" novalidate>
              <div class="card-body">
                  <div class="row">
                      <div class="col">                          
                          <input (change)="getFileDetails($event)"  type="file" id="fileInput" formControlName="fileInput" 
                          name="fileInput">                          
                          <div *ngIf="uf.fileInput.touched && uf.fileInput.invalid" class="alert alert-danger-custom">
                            <div *ngIf="uf.fileInput.errors?.required">
                              File is required.
                            </div>
                            <div *ngIf="uf.fileInput.errors?.mustBeCSVFile">
                              File should be CSV.
                            </div>
                            <div *ngIf="uf.fileInput.errors?.mustBeLessThan2MB">
                              File should be less than 3 MB.
                            </div>
                          </div>
                      </div>
                      <div class="col w-10 justify-content-around d-flex flex-column">
                          <div>
                              <button [disabled]="!angFormUpload.valid" type="submit" class="btn btn-primary btn-sm">
                                  <i class="fa fa-upload"></i> Upload
                              </button>
                          </div>                          
                      </div>
                  </div>
              </div>
          </form>
      </div>
    </div>
  </div>
</div>`
})
export class UserUploadComponent implements OnInit {
  angFormUpload: FormGroup;
  selectedFiles?: FileList;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private userService: UserService
  ) {
    this.angFormUpload = this.fb.group({
      fileInput: new FormControl(null, [
        Validators.required,
      ]),
    });
  }
  ngOnInit() { }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get uf() { return this.angFormUpload.controls; }

  getFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angFormUpload.controls['fileInput'];
      if (file.errors && !file.errors.mustBeCSVFile && !file.errors.mustBeLessThan2MB) {
        return;
      }
      console.log(type != "text/csv");
      if (type != "text/csv" && size > (3145728)) {
        file.setErrors({ mustBeCSVFile: true, mustBeLessThan2MB: true });
      } else if (type == "text/csv" && size > (3145728)) {
        file.setErrors({ mustBeCSVFile: false, mustBeLessThan2MB: true });
      } else if (type != "text/csv" && size <= (3145728)) {
        file.setErrors({ mustBeCSVFile: true, mustBeLessThan2MB: false });
      } else if (type == "text/csv" && size <= (3145728)) {
        file.setErrors(null);
        this.selectedFiles = event.target.files;
      }
      console.log('Name: ' + name + "\n" +
        'Type: ' + type + "\n" +
        'Last-Modified-Date: ' + modifiedDate + "\n" +
        'Size: ' + Math.round(size / 1024) + " KB");
    }
  }

  uploadFile() {
    if (this.angFormUpload.valid) {
      const file: File | null = this.selectedFiles.item(0);
      const formData: FormData = new FormData();
      formData.append('file', file, file.name);
      let headers = new Headers();
      headers.append('Content-Type', 'multipart/form-data');
      headers.set('Accept', 'application/json');
      //console.log(formData);
      this.userService
        .uploadUsersService(formData, { 'headers': headers })
        .subscribe((data: any) => {
          this.bsModalRef.hide();
        });
    } else {
      console.log("Invalid Form!");
    }
  }
}