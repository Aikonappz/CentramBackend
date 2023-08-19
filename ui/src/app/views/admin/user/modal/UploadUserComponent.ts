import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef } from "ngx-bootstrap/modal";
import { UserService } from "../../../../service/UserService";


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
  export class UploadUserComponent implements OnInit {
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
        //console.log(type != "text/csv");
        let validMimeTpes = ["text/csv", "application/vnd.ms-excel",];
        if (!validMimeTpes.includes(type)) {
          file.setErrors({ mustBeCSVFile: true, mustBeLessThan2MB: false });
        } else if (size > (3145728)) {
          file.setErrors({ mustBeCSVFile: false, mustBeLessThan2MB: true });
        } else {
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