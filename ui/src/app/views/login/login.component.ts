import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { AppUtility } from '../../config/AppUtility';
import { AuthRequest } from '../../model/AuthRequest';
import { LoggedInUser } from '../../model/LoggedInUser';
import { UserService } from '../../service/UserService';


@Component({
  selector: 'app-dashboard',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.scss']
})
export class LoginComponent implements OnInit {
  authRequest: AuthRequest;
  angForm = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email,
      //Validators.pattern(this.dateRegx),
    ]),
    password: new FormControl('', [
      Validators.required,
      //Validators.pattern(this.dateRegx),
    ])
  });

  constructor(
    private titleService: Title,
    private router: Router,
    private userService: UserService
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
    this.authRequest = new AuthRequest();
  }

  ngOnInit() {
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

  get f() {
    return this.angForm.controls;
  }

  formSubmit() {
    if (this.angForm.valid) {
      //console.log(this.angForm);
      this.authRequest.username = this.angForm.controls['email'].value;
      this.authRequest.password = this.angForm.controls['password'].value;
      //console.log(this.authRequest);
      this.callSignInService();
    } else {
      console.log("Invalid Form!");
    }
  }

  callSignInService() {
    this.userService
      .signInService(this.authRequest)
      .subscribe((data: LoggedInUser) => {
        localStorage.setItem(AppUtility.LOGED_IN_PROFILE_JWT, data.jwtToken);
        data.jwtToken = null;
        AppUtility.APP_DEFAULT_TIMEZONE = data.timeZone;
        localStorage.setItem(AppUtility.LOGED_IN_PROFILE, JSON.stringify(data));
        console.log(AppUtility.APP_DEFAULT_TIMEZONE);
        //console.log(data);
        let lastVisitedPage = localStorage.getItem(AppUtility.LOGED_IN_LAST_VISIT);
        // if (lastVisitedPage != null) {
        //   console.log(lastVisitedPage);
        //   this.router.navigate(["#" + lastVisitedPage]);
        // } else {
        //   this.router.navigate(['/dashboard']);
        // }
        this.router.navigate(['/dashboard']);
      });
  }
}

