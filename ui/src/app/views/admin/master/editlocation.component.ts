import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { LocationVO } from '../../../model/LocationVO';
import { MiscService } from '../../../service/MiscService';
import { Status } from '../../../model/enumerator/Status';
import { countryWithTimeZones } from '../../../model/_countryWithTimeZones';
import { CountryWithTimeZone } from '../../../model/CountryWithTimeZone';
import { AppUtility } from '../../../config/AppUtility';
import { StartEndTimeValidation } from '../../../validator/StartEndTimeValidation';
import { LoggedInUserService } from '../../../service/LoggedInUserService';
import { Account } from '../../../model/Account';

@Component({
  selector: 'app-editlocation',
  templateUrl: './editlocation.component.html',
  styleUrls: ['./editlocation.component.scss']
})
export class EditLocationComponent implements OnInit {
  moduleName: string = "LOCATION";
  //actions: string[] = ["READ", "DELETE", "SEARCH", "WRITE"];
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  loc: LocationVO;
  countries = countryWithTimeZones;
  uniqueCountries: any[] = [];
  filterdCountry: CountryWithTimeZone[];
  timeList: any[] = [];
  angForm: FormGroup;
  accounts: Account[] = [];

  constructor(
    private loggedInUserService: LoggedInUserService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private miscService: MiscService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.angForm = this.fb.group({
      account: new FormControl(null, [
        Validators.required,
      ]),
      country: new FormControl(null, [
        Validators.required,
      ]),
      timezone: new FormControl(null, [
        Validators.required,
      ]),
      state: new FormControl(null, [
        Validators.maxLength(255),
      ]),
      city: new FormControl(null, [
        Validators.maxLength(255),
      ]),
      name: new FormControl(null, [
        Validators.required,
        Validators.maxLength(255),
      ]),
      officeName: new FormControl(null, [
        //Validators.required,
        Validators.maxLength(255),
      ]),
      opsStartTime: new FormControl(null, [
        Validators.required,
      ]),
      opsEndTime: new FormControl(null, [
        Validators.required,
      ]),
      status: new FormControl('ACTIVE', [
      ]),
    }, {
      validators: StartEndTimeValidation('opsStartTime', 'opsEndTime')
    });
    this.loc = new LocationVO();
    this.loc.status = this.defaultStatus;
    let timeList = AppUtility.getDayHourList(30);
    for (let k in timeList) {
      this.timeList.push({ id: timeList[k], label: timeList[k] });
    }
    const uniqueCountryList = [...new Set(this.countries.map(item => item.country_name))];
    //console.log(uniqueCountryList);
    for (let k in uniqueCountryList) {
      this.uniqueCountries.push({ id: uniqueCountryList[k], label: uniqueCountryList[k] });
    }
    this.uniqueCountries.sort(function (a, b) {
      if (b.label > a.label) return -1;
      if (a.label > b.label) return 1;
      return 0;
    });
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
    if (!this.route.snapshot.paramMap.has('id')) {
      this.miscService
        .accountsService()
        .subscribe((data: any) => {
          this.accounts = data.content;
          for (let i = 0; i < this.accounts.length; i++) {
            this.accounts[i].label = this.accounts[i].name + " [" + this.accounts[i].accountNo + "]";
          }
          if (this.accounts.length == 1)
            this.angForm.get('account').setValue(this.accounts[0].id);
        });
    } else {
      this.miscService
        .accountsService()
        .subscribe((data: any) => {
          this.accounts = data.content;
          for (let i = 0; i < this.accounts.length; i++) {
            this.accounts[i].label = this.accounts[i].name + " [" + this.accounts[i].accountNo + "]";
          }
          this.entityId = Number(this.route.snapshot.paramMap.get('id'));
          this.newEntity = false;
          this.callGetLocationService(this.entityId);
        });
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      if (this.statusFlag === false) {
        let res = window.confirm("Do you really want to change the status?")
        if (!res) {
          return;
        }
      }
      //console.log(this.angForm);
      this.loc.country = this.angForm.controls['country'].value;
      this.loc.timezone = this.angForm.controls['timezone'].value;
      this.loc.state = this.angForm.controls['state'].value;
      this.loc.city = this.angForm.controls['city'].value;
      this.loc.name = this.angForm.controls['name'].value;
      this.loc.opsStartTime = this.angForm.controls['opsStartTime'].value;
      this.loc.opsEndTime = this.angForm.controls['opsEndTime'].value;
      this.loc.officeName = this.angForm.controls['officeName'].value;
      for (let i = 0; i < this.accounts.length; i++) {
        if (this.accounts[i].id == this.angForm.controls['account'].value) {
          this.loc.account = { id: this.accounts[i].id, version: this.accounts[i].version } as Account;
        }
      }
      /* process department and location */
      this.loc.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user.status);
      this.callSaveLocationService();
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveLocationService() {
    this.miscService
      .saveLocationService(this.loc)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/admin/master/location']);
      });
  }

  callGetLocationService(id: number) {
    this.miscService
      .locationService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.loc.id = data.id;
        this.loc.name = data.name;
        this.loc.opsStartTime = data.opsStartTime;
        this.loc.opsEndTime = data.opsEndTime;
        this.loc.country = data.country;
        this.loc.timezone = data.timezone;
        this.loc.state = data.state;
        this.loc.city = data.city;
        this.loc.status = data.status;
        this.loc.version = data.version;
        this.loc.officeName = data.officeName;
        this.loc.account = data.account;
        //console.log(JSON.stringify(this.user));

        this.populateTimezone({ id: this.loc.country });
        this.angForm.get('name').setValue(this.loc.name);
        this.angForm.get('opsStartTime').setValue(this.loc.opsStartTime);
        this.angForm.get('opsEndTime').setValue(this.loc.opsEndTime);
        this.angForm.get('timezone').setValue(this.loc.timezone);
        this.angForm.get('state').setValue(this.loc.state);
        this.angForm.get('city').setValue(this.loc.city);
        this.angForm.get('country').setValue(this.loc.country);
        this.angForm.get('officeName').setValue(this.loc.officeName);
        this.angForm.get('account').setValue(this.loc.account.id);

        this.statusFlag = String(this.loc.status) == 'ACTIVE' ? true : false;
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }

  @ViewChild("country") country;
  populateTimezone(countryVal) {
    if (typeof countryVal !== 'undefined') {
      let c = 0;
      this.filterdCountry = [];
      for (let i = 0; i < this.countries.length; i++) {
        if (this.countries[i].country_name == countryVal.id) {
          this.filterdCountry[c] = this.countries[i];
          c++;
        }
      }
    }
  }

}