import { Component, OnInit, ViewChild, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { MiscService } from '../../service/MiscService';
import { HolidayCalender } from '../../model/HolidayCalender';

@Component({
  selector: 'app-editholidaycalender',
  templateUrl: './editholidaycalender.component.html',
  styleUrls: ['./editholidaycalender.component.scss']
})
export class EditHolidayCalenderComponent implements OnInit {
  newEntity: boolean = true;
  defaultStatus: any = 'ACTIVE';
  statusFlag: boolean = true;
  entityId: number;
  hc: HolidayCalender;
  yearList: number[];
  angForm: FormGroup;
  selectedFiles?: FileList;
  constructor(
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
      year: new FormControl('', [
        Validators.required,
      ]),
      fileInput: new FormControl(null, [
        Validators.required,
      ]),
    });
    this.hc = new HolidayCalender();
    let c = 0;
    this.yearList = [];
    for (let i = 2021; i <= 2099; i++) {
      this.yearList.push(i);
    }
  }

  getFileDetails(event) {
    for (var i = 0; i < event.target.files.length; i++) {
      var name = event.target.files[i].name;
      var type = event.target.files[i].type;
      var size = event.target.files[i].size;
      var modifiedDate = event.target.files[i].lastModifiedDate;
      const file = this.angForm.controls['fileInput'];
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
      this.newEntity = false;
      this.entityId = Number(this.route.snapshot.paramMap.get('id'));
      this.callholidayCalenderService(this.entityId);
    }
  }

  ngAfterViewInit() { }

  ngAfterContentInit() { }

  get f() { return this.angForm.controls; }

  formSubmit() {
    if (this.angForm.valid) {
      this.hc.year = this.angForm.controls['year'].value;
      this.hc.holidays = [];
      const file: File | null = this.selectedFiles.item(0);
      const formData: FormData = new FormData();
      formData.append('file', file, file.name);
      formData.append('holidayCalender', new Blob([JSON.stringify(this.hc)], { type: "application/json" }));
      let headers = new Headers();
      headers.append('Content-Type', 'multipart/form-data');
      headers.set('Accept', 'application/json');
      //console.log(this.angForm);
      //this.hc.name = this.angForm.controls['name'].value;
      //this.hc.status = this.statusFlag == false ? Status['INACTIVE'] : Status['ACTIVE'];
      //console.log(this.user.status);
      //console.log(this.angForm.controls['status'].value);
      this.callSaveHolidayCalenderService(formData);
    } else {
      console.log("Invalid Form!");
    }
  }

  goBack() { this._location.back(); }

  callSaveHolidayCalenderService(formData: FormData) {
    this.miscService
      .saveHolidayCalenderService(formData)
      .subscribe((data: any) => {
        //console.log(data);
        this.router.navigate(['/masters/calender']);
      });
  }

  callholidayCalenderService(id: number) {
    this.miscService
      .holidayCalenderService(id)
      .subscribe((data: any) => {
        //console.log(JSON.stringify(data));
        this.hc.id = data.id;
        this.hc.year = data.year;
        this.hc.holidays = data.holidays;
        // this.hc.status = data.status;
        this.hc.version = data.version;
        //console.log(JSON.stringify(this.user));
        this.angForm.get('year').setValue(this.hc.year);
        this.angForm.get('year').disable();
        //this.angForm.markAllAsTouched();
      });
  }

  // @ViewChild("status") status;
  // onChange(status: boolean, inp: string) {
  //   //this.statusFlag = this.active_status.nativeElement.checked;
  //   //console.log(status);
  //   //console.log(inp);
  //   this.statusFlag = status;
  // }
}