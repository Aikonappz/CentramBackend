import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { CommonResponse } from '../../model/CommonResponse';
import { RequestDemoDTO } from '../../model/RequestDemoDTO';
import { MiscService } from '../../service/MiscService';

@Component({
  selector: 'app-dashboard',
  templateUrl: './request-demo.component.html',
  styleUrls: ['./request-demo.component.scss']
})
export class RequestDemoComponent implements OnInit {
  phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
  requestDemoDTO: RequestDemoDTO;
  angForm = new FormGroup({
    name: new FormControl('', [Validators.required,]),
    email: new FormControl('', [
      Validators.required,
      Validators.email,
      //Validators.pattern(this.dateRegx),
    ]),
    phone: new FormControl('', [
      Validators.required,
      Validators.pattern(this.phoneRegex),
    ]),
  });
  constructor(titleService: Title, private router: Router, private miscService: MiscService,) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
    this.requestDemoDTO = new RequestDemoDTO;
  }

  callRequestDemoService() {
    this.miscService
      .requestDemoService(
        this.requestDemoDTO
      )
      .subscribe((data: CommonResponse) => {
        this.router.navigate(['/']);
        //console.log(data);
        //this.angForm.reset();
        //this.toggleStockAddMode();
      });
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
  }

  get f() {
    return this.angForm.controls;
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.requestDemoDTO.name = this.angForm.controls['name'].value;
      this.requestDemoDTO.email = this.angForm.controls['email'].value;
      this.requestDemoDTO.phone = this.angForm.controls['phone'].value;
      //console.log(this.requestDemoDTO);
      this.callRequestDemoService();
    } else {
      console.log("Invalid Form!");
    }
  }
}
