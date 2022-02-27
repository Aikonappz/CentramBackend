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
import { OrganisationDetailModal } from './modal/OrganisationDetailModal';

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
  statusFlag: string = 'ALL';
  drStatus: { isOpen: boolean } = { isOpen: false };
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
      status: new FormControl('ALL', []),
    });
    this.org = new Organisation();
    this.org.status = this.defaultStatus;
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
    this.drStatus.isOpen = false;
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
  @ViewChild("status") status;
  onChange(inp: string) {
    this.statusFlag = inp;
  }
  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.org.name = this.angForm.controls['orgName'].value;
      this.org.status = this.statusFlag;
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
    this.modalRef = this.modalService.show(OrganisationDetailModal,
      Object.assign({}, config, { initialState })
    );
  }

}
