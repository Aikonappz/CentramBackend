import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AppUtility } from '../../config/AppUtility';
import { AuthRequest } from '../../model/AuthRequest';
import { LoggedInUser } from '../../model/LoggedInUser';
import { Permission } from '../../model/Permssion';
import { ClientStorageService } from '../../service/ClientStorageService';
import { UserService } from '../../service/UserService';


@Component({
  selector: 'app-sso-login',
  templateUrl: 'sso-login.component.html',
  styleUrls: ['sso-login.component.scss']
})
export class SSOLoginComponent implements OnInit {
  authRequest: AuthRequest;
  private REST_API_SERVER = environment.appServiceEndpoint;
  private hasError: boolean = false;
  constructor(
    private route: ActivatedRoute,
    private titleService: Title,
    private router: Router,
    private clientStorageService: ClientStorageService,
    private http: HttpClient,
  ) {

    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
    this.authRequest = new AuthRequest();
    this.route.params.subscribe(params => {
      if (this.route.snapshot.paramMap.get("principal") != null && this.route.snapshot.paramMap.get("principal") != '') {
        //alert(this.route.snapshot.paramMap.get("principal"));
        console.log(this.route.snapshot.paramMap.get("principal"));
        this.authRequest.username = this.route.snapshot.paramMap.get("principal");
        this.authRequest.password = "NA";
        //console.log(JSON.stringify(this.authRequest));
        this.callSSOSignInService();
      } else {
        this.router.navigate(['/sign-in']);
      }
    });
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

  callSSOSignInService() {
    this.http
      .post(this.REST_API_SERVER + '/v1/user/sso-sign-in', this.authRequest, {})
      .subscribe(
        d => {
          let data = d as LoggedInUser;
          //console.log(data);
          data.jwtToken = btoa(data.jwtToken);
          for (let i = 0; i < data.modulePermissions.length; i++) {
            data.modulePermissions[i] = new Permission(data.modulePermissions[i]);
          }
          this.clientStorageService.set(AppUtility.LOGGED_IN_PROFILE, JSON.stringify(data));
          //console.log(this.clientStorageService.get(AppUtility.LOGGED_IN_LAST_VISIT));
          let lastVisitedPage = this.clientStorageService.get(AppUtility.LOGGED_IN_LAST_VISIT);
          if (lastVisitedPage != null) {
            //console.log(lastVisitedPage);
            this.router.navigate([lastVisitedPage]);
          } else {
            this.router.navigate(['/dashboard']);
          }
          this.hasError = false;
          //console.log(JSON.stringify(data));
          //localStorage.setItem(AppUtility.LOGGED_IN_PROFILE_JWT, btoa(data.jwtToken));
          //data.jwtToken = null;
        },
        error => {
          this.hasError = true;
          setTimeout(() => {
            this.router.navigate(['/sign-in']);
          },
            5000);
        }
      );
  }
}

