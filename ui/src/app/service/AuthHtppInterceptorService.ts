import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, } from '@angular/common/http';
@Injectable({
  providedIn: 'root',
})
export class AuthHtppInterceptorService implements HttpInterceptor {
  constructor() { }
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    //if (sessionStorage.getItem('token')) {
      // req = req.clone({
      //   setHeaders: {
      //     Authorization: 'Bearer ' + sessionStorage.getItem('token'),
      //     //Accept: 'application/json',
      //     //'Content-Type': 'application/json'
      //   },
      // });
    //}
    return next.handle(req);
  }
}