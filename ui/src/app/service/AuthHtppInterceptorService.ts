import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpResponse, HttpErrorResponse, } from '@angular/common/http';
import { AppUtility } from '../config/AppUtility';
import { SpinnerService } from './SpinnerService';
import { tap } from 'rxjs/operators';
@Injectable({
  providedIn: 'root',
})
export class AuthHtppInterceptorService implements HttpInterceptor {
  constructor(
    private spinnerService: SpinnerService
  ) { }
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    if (localStorage.getItem(AppUtility.LOGED_IN_PROFILE_JWT)) {
      req = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE_JWT)),
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