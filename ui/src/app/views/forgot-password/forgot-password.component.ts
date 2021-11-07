import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { AuthRequest } from '../../model/AuthRequest';
import { CommonResponse } from '../../model/CommonResponse';
import { UserService } from '../../service/UserService';

@Component({
  selector: 'app-dashboard',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
  authRequest: AuthRequest;
  angForm = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email,
      //Validators.pattern(this.dateRegx),
    ]),
  });

  constructor(private titleService: Title, private router: Router, private userService: UserService) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
    this.authRequest = new AuthRequest();
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
      this.authRequest.username = this.angForm.controls['email'].value;
      //console.log(this.authRequest);
      this.callForgotPasswordService();
    } else {
      console.log("Invalid Form!");
    }
  }

  callForgotPasswordService() {
    this.userService
      .requestForgotPasswordService(
        this.authRequest
      )
      .subscribe((data: CommonResponse) => {
        //console.log(data);
        //this.angForm.reset();
        //this.toggleStockAddMode();
        this.router.navigate(['/']);
      });
  }

}
