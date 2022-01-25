import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { Setting } from '../../model/Setting';
import { OrganisationService } from '../../service/OrganisationService';
import { TicketAllocationType } from '../../model/enumerator/TicketAllocationType';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';

@Component({
  selector: 'app-orgsettings',
  templateUrl: './orgsettings.component.html',
  styleUrls: ['./orgsettings.component.scss']
})
export class OrgSettingsComponent implements OnInit {
  moduleName: string = "ORGANISATION";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  alphaNumericRegex = /^[a-z0-9]+$/i;
  angForm: FormGroup;
  setting: Setting;
  saved: boolean = false;
  ticketAllocationTypes: any;
  loggedInUser: LoggedInUser;
  canEdit: boolean = true;
  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private service: OrganisationService,
    private miscService: MiscService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.setting = new Setting();
    this.ticketAllocationTypes = Object.values(TicketAllocationType)
      .filter((value) => typeof value === "string")
      .map((value) => (value as string));
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    //console.log(this.loggedInUser);
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
    this.angForm = this.fb.group({
      incidentPrefix: new FormControl('', [
        Validators.maxLength(5),
        Validators.pattern(this.alphaNumericRegex),
      ]),
      assetPrefix: new FormControl('', [
        Validators.maxLength(5),
        Validators.pattern(this.alphaNumericRegex),
      ]),
      ticketAllocationType: new FormControl('', [

      ]),
    });
    this.callgetOrganisationSettingService()
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.setting.assetPrefix = this.angForm.controls['assetPrefix'].value;
      this.setting.incidentPrefix = this.angForm.controls['incidentPrefix'].value;
      this.callsetOrganisationSettingService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callsetOrganisationSettingService() {
    this.setting.assetPrefix = this.angForm.controls['assetPrefix'].value;
    this.setting.incidentPrefix = this.angForm.controls['incidentPrefix'].value;
    //this.setting.ticketAllocationType = TicketAllocationType[String(this.angForm.controls['ticketAllocationType'].value)];
    this.setting.ticketAllocationType = 1;
    this.service
      .setOrganisationSettingService(this.setting)
      .subscribe((data: any) => {
        this.saved = true;
        //console.log(data);
        //this.router.navigate(['/user']);
      });
  }

  callgetOrganisationSettingService() {
    this.service
      .getOrganisationSettingService()
      .subscribe((data: Setting) => {
        if (data != null) {
          this.canEdit = false;
          this.setting = data;
          this.angForm.get('assetPrefix').setValue(this.setting.assetPrefix);
          this.angForm.get('incidentPrefix').setValue(this.setting.incidentPrefix);
          this.angForm.get('ticketAllocationType').setValue(this.setting.ticketAllocationType);
        } else {
          this.canEdit = true;
        }
      });
  }
}