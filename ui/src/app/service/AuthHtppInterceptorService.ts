import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpResponse, HttpErrorResponse, } from '@angular/common/http';
import { AppUtility } from '../config/AppUtility';
import { SpinnerService } from './SpinnerService';
import { tap } from 'rxjs/operators';
import { LoggedInUserService } from './LoggedInUserService';
import { LoggedInUser } from '../model/LoggedInUser';
@Injectable({
  providedIn: 'root',
})
export class AuthHtppInterceptorService implements HttpInterceptor {
  private loggedInUser: LoggedInUser;
  constructor(
    private spinnerService: SpinnerService,
    private loggedInUserService: LoggedInUserService,
  ) { }
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    if (this.loggedInUser != null && this.loggedInUser.jwtToken != null && this.loggedInUser.jwtToken.replace(/\s/g, "") != "") {
      req = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + atob(this.loggedInUser.jwtToken),
          //Accept: 'application/json',
          //'Content-Type': 'application/json'
        },
      });
    }
    this.spinnerService.requestStarted();
    return next.handle(req)
      .pipe(
        tap(
          (event) => {
            if (event instanceof HttpResponse) {
              this.spinnerService.requestedEnded();
            }
          },
          (error: HttpErrorResponse) => {
            this.spinnerService.resetSpinner();
            throw error;
          }
        )
      );
  }
}