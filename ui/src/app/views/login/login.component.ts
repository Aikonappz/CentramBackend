import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { AppUtility } from '../../config/AppUtility';
import { AuthRequest } from '../../model/AuthRequest';
import { LoggedInUser } from '../../model/LoggedInUser';
import { Permission } from '../../model/Permssion';
import { ClientStorageService } from '../../service/ClientStorageService';
import { UserService } from '../../service/UserService';


@Component({
  selector: 'app-dashboard',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.scss']
})
export class LoginComponent implements OnInit {
  sessionTimeOut: boolean = false;
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
    private route: ActivatedRoute,
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private clientStorageService: ClientStorageService,
  ) {
    this.route.params.subscribe(params => {
      this.sessionTimeOut = this.route.snapshot.paramMap.get('mode') === 'sessionTimeOut' ? true : false;
    });
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
    this.removeClientData();
  }

  removeClientData() {
    this.clientStorageService.remove(AppUtility.APP_LAST_ACTION_KEY);
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
      this.authRequest.password = btoa(this.angForm.controls['password'].value);
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
        data.jwtToken = btoa(data.jwtToken);
        for (let i = 0; i < data.modulePermissions.length; i++) {
          data.modulePermissions[i] = new Permission(data.modulePermissions[i]);
        }
        this.clientStorageService.set(AppUtility.LOGGED_IN_PROFILE, JSON.stringify(data));
        //console.log(this.clientStorageService.get(AppUtility.LOGGED_IN_LAST_VISIT));
        let lastVisitedPage = this.clientStorageService.get(AppUtility.LOGGED_IN_LAST_VISIT);
        if (lastVisitedPage != null) {
          console.log(lastVisitedPage);
          this.router.navigate([lastVisitedPage]);
        } else {
          this.router.navigate(['/dashboard']);
        }
        //console.log(JSON.stringify(data));
        //localStorage.setItem(AppUtility.LOGGED_IN_PROFILE_JWT, btoa(data.jwtToken));
        //data.jwtToken = null;
      });
  }
}

