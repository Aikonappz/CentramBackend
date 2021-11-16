import { Component, OnInit, ViewChild, } from '@angular/core';

import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { LocationVO } from '../../model/LocationVO';

import { MiscService } from '../../service/MiscService';

import { Status } from '../../model/enumerator/Status';

import { countryWithTimeZones } from '../../model/_countryWithTimeZones';
import { CountryWithTimeZone } from '../../model/CountryWithTimeZone';

@Component({
  selector: 'app-editlocation',
  templateUrl: './editlocation.component.html',
  styleUrls: ['./editlocation.component.scss']
})
export class EditLocationComponent implements OnInit {
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  loc: LocationVO;
  countries = countryWithTimeZones;
  filterdCountry: CountryWithTimeZone[];
  angForm = new FormGroup({
    country: new FormControl('', [
      Validators.required,
    ]),
    timezone: new FormControl('', [
      Validators.required,
    ]),
    state: new FormControl('', [
      Validators.maxLength(255),
    ]),
    city: new FormControl('', [
      Validators.maxLength(255),
    ]),
    name: new FormControl('', [
      Validators.required,
      Validators.maxLength(255),
    ]),
    status: new FormControl('ACTIVE', [
    ]),
  });
  constructor(
    private route: ActivatedRoute,
    private _location: Location,
    private titleService: Title,
    private router: Router,
    private service: MiscService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loc = new LocationVO();
    this.loc.status = this.defaultStatus;
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

    } else {
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.newEntity = false;
      this.callGetLocationService(this.entityId);
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.loc.country = this.angForm.controls['country'].value;
      this.loc.timezone = this.angForm.controls['timezone'].value;
      this.loc.state = this.angForm.controls['state'].value;
      this.loc.city = this.angForm.controls['city'].value;
      this.loc.name = this.angForm.controls['name'].value;

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
    this.service
      .saveLocationService(this.loc)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/masters/location']);
      });
  }

  callGetLocationService(id: number) {
    this.service
      .locationService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.loc.id = data.id;
        this.loc.name = data.name;
        this.loc.country = data.country;
        this.loc.timezone = data.timezone;
        this.loc.state = data.state;
        this.loc.city = data.city;
        this.loc.status = data.status;
        this.loc.version = data.version;
        //console.log(JSON.stringify(this.user));

        this.populateTimezone(this.loc.country);
        this.angForm.get('name').setValue(this.loc.name);
        this.angForm.get('timezone').setValue(this.loc.timezone);
        this.angForm.get('state').setValue(this.loc.state);
        this.angForm.get('city').setValue(this.loc.city);
        this.angForm.get('country').setValue(this.loc.country);

        this.statusFlag = String(this.loc.status) == 'ACTIVE' ? true : false;
        //this.angForm.get('status').setValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        //this.angForm.get('status').patchValue(String(Status[this.user.status]) == 'ACTIVE' ? true : false);
        this.angForm.markAllAsTouched();
      });
  }

  @ViewChild("status") status;
  onChange(status: boolean, inp: string) {
    this.statusFlag = status;
  }

  @ViewChild("country") country;
  populateTimezone(countryVal) {
    let c = 0;
    if (countryVal != "") {
      this.filterdCountry = [];
      for (let i = 0; i < this.countries.length; i++) {
        if (this.countries[i].country_name == countryVal) {
          this.filterdCountry[c] = this.countries[i];
          c++;
        }
      }
    }
  }
}