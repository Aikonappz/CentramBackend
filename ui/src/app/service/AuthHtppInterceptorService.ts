import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, } from '@angular/common/http';
import { AppUtility } from '../config/AppUtility';
@Injectable({
  providedIn: 'root',
})
export class AuthHtppInterceptorService implements HttpInterceptor {
  constructor() { }
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    if (localStorage.getItem(AppUtility.LOGED_IN_PROFILE_JWT)) {
      req = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + localStorage.getItem(AppUtility.LOGED_IN_PROFILE_JWT),
          //Accept: 'application/json',
          //'Content-Type': 'application/json'
        },
      });
    }
    return next.handle(req);
  }
}