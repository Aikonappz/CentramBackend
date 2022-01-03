import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { Status } from '../../model/enumerator/Status';
import { Organisation } from '../../model/Organisation';
import { OrganisationDataSource } from '../../service/datasource/OrganisationDataSource';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { OrganisationService } from '../../service/OrganisationService';

@Component({
  selector: 'app-organisation',
  templateUrl: './organisation.component.html',
  styleUrls: ['./organisation.component.scss']
})
export class OrganisationComponent implements OnInit, OnDestroy {
  moduleName: string = "ORGANIZATION";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  displayedColumns = ['name', 'addressDtl', 'licence', 'status', 'action'];
  private datasource: OrganisationDataSource;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  angForm: FormGroup;
  org: Organisation;
  defaultStatus: any = 'ALL';
  statusFlag: boolean = true;
  status: { isOpen: boolean } = { isOpen: false };
  disabled: boolean = false;
  isDropup: boolean = true;
  autoClose: boolean = false;
  modalRef: BsModalRef;
  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: OrganisationService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      orgName: new FormControl('', [
        Validators.maxLength(255)
      ]),
      status: new FormControl('ALL', [

      ]),
    });
    this.org = new Organisation();
    this.org.status = this.defaultStatus;
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
    this.status.isOpen = !this.status.isOpen;
  }

  change(value: boolean): void {
    this.status.isOpen = value;
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
    this.datasource = new OrganisationDataSource(this.service);
    this.datasource.loadData();
  }

  ngOnDestroy() {
    this.status.isOpen = false;
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
        tap(() => this.loadData({}))
      )
      .subscribe();
  }

  loadData(req = {}) {
    this.datasource.loadData(this.paginator.pageIndex, this.paginator.pageSize, req);
  }

  updateStatus(org: Organisation) {
    let res = window.confirm("Are you sure?")
    if (res) {
      let elm = document.getElementById("id-status-" + org.id);
      let val = ((elm.getAttribute("data-label") == 'ACTIVE') ? Status.INACTIVE : Status.ACTIVE);
      this.service
        .updateStatusService([org.id], val, {})
        .subscribe((data: any) => {
          elm.setAttribute("data-label", Status[val]);
          elm.textContent = Status[val];
        });
    }
  }
  edit(org: Organisation) {
    this.router.navigate(['/organization/edit/' + org.id]);
  }
  add() {
    this.router.navigate(['/organization/add']);
  }
  loadPage() {
    this.angForm.reset();
    this.loadData({});
    this.org.status = this.defaultStatus;
  }
  formatDate(d: string) {
    if (d != null && d != "") {
      return moment(d).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.org.name = this.angForm.controls['orgName'].value;
      this.loadData({ "name": this.org.name == null ? '' : this.org.name, "status": this.org.status });
      //console.log(JSON.stringify(this.org));
    } else {
      console.log("Invalid Form!");
    }
  }

  view(element: Organisation) {
    const config: ModalOptions = {
      backdrop: 'static',
      keyboard: false,
      animated: true,
      ignoreBackdropClick: true,
      class: 'modal-bg',
    };
    const initialState = {
      org: element
    };
    this.modalRef = this.modalService.show(ViewOrganisationDetail,
      Object.assign({}, config, { initialState })
    );
  }

}

@Component({
  selector: 'modal-content',
  template: `<div class="modal-header">
  <h6 class="modal-title pull-left"><i class="icon-eye"></i> View Organisation Details</h6>
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
                      <td>{{org.name}}</td>
                  </tr>
                  <tr>
                      <td>Add1</td>
                      <td>{{org.add1}}</td>
                  </tr>
                  <tr>
                      <td>Add2</td>
                      <td>{{org.add2}}</td>
                  </tr>
                  <tr>
                      <td>City</td>
                      <td>{{org.city}}</td>
                  </tr>
                  <tr>
                      <td>Pincode</td>
                      <td>{{org.pincode}}</td>
                  </tr>
                  <tr>
                      <td>PAN</td>
                      <td>{{org.pan}}</td>
                  </tr>
                  <tr>
                      <td>TAN</td>
                      <td>{{org.tan}}</td>
                  </tr>
                  <tr>
                      <td>GSTIN</td>
                      <td>{{org.gstin}}</td>
                  </tr>
                  <tr>
                      <td>License Type</td>
                      <td>{{org.licenseType}}</td>
                  </tr>
                  <tr>
                      <td>License Start</td>
                      <td>{{formatDate(org.licenseStart)}}</td>
                  </tr>
                  <tr>
                      <td>License End</td>
                      <td>{{formatDate(org.licenseEnd)}}</td>
                  </tr>
                  <tr>
                      <td>Contact Persons</td>
                      <td>
                      <p *ngIf="org.contactPersons.length > 0">Key person 1 name - {{org.contactPersons[0].name}}</p>
                      <p *ngIf="org.contactPersons.length > 0">Key person 1 email - {{org.contactPersons[0].email}}</p>
                      <p *ngIf="org.contactPersons.length > 0">Key person 1 contact - {{org.contactPersons[0].contactNo}}</p>
                      <hr/>
                      <p *ngIf="org.contactPersons.length > 1">Key person 2 name - {{org.contactPersons[1].name}}</p>
                      <p *ngIf="org.contactPersons.length > 1">Key person 2 email - {{org.contactPersons[1].email}}</p>
                      <p *ngIf="org.contactPersons.length > 1">Key person 2 contact - {{org.contactPersons[1].contactNo}}</p>                         
                      </td>
                  </tr>
                  <tr>
                      <td>Status</td>
                      <td>{{org.status}}</td>
                  </tr>
              </table>
          </div>
      </div>
  </div>
</div>`
})
export class ViewOrganisationDetail implements OnInit {
  org: Organisation;
  constructor(
    private fb: FormBuilder,
    public bsModalRef: BsModalRef,
    private service: OrganisationService,
    public options: ModalOptions,
  ) {
  }
  ngOnInit() {
  }

  ngAfterViewInit() {
  }

  ngAfterContentInit() {
  }

  formatDate(d: string) {
    if (d != null && d != "") {
      return moment.utc(d).tz(AppUtility.APP_DEFAULT_TIMEZONE).format(AppUtility.APP_VIEW_DATE_FORMAT);
    }
    return null;
  }

}
