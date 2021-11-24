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
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  modalRef: BsModalRef;
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
    private userService: UserService,
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
          //alert('Please disable your Pop-up blocker and try again.');
        }
      });
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
      formData.append('file', file);
      //console.log(formData);
      this.userService
        .uploadUsersService(formData)
        .subscribe((data: any) => {
          this.bsModalRef.hide();
        });
    } else {
      console.log("Invalid Form!");
    }
  }
}