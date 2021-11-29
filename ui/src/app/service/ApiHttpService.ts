import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, } from '@angular/common/http';
import { throwError, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AppUtility } from '../config/AppUtility';
import { Router } from '@angular/router';
import { SpinnerService } from './SpinnerService';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { ErrormessageComponent } from '../views/errormessage/errormessage.component';
@Injectable({
    providedIn: 'root'
})
export class ApiHttpService {
    public bsModalRef: BsModalRef;
    private REST_API_SERVER = AppUtility.API_ENDPOINT;

    constructor(
        private http: HttpClient,
        private router: Router,
        private modalService: BsModalService,
    ) { }

    public get(url: string, options?: any): Observable<any> {
        return this.http
            .get<any>(this.REST_API_SERVER + url, options)
            .pipe(catchError(this.handleError.bind(this)));
    }

    public post(url: string, data: any, options?: any): Observable<any> {
        return this.http
            .post(this.REST_API_SERVER + url, data, options)
            .pipe(catchError(this.handleError.bind(this)));
        //.post(this.REST_API_SERVER + url, data, { observe: 'response' })
    }

    public put(url: string, data: any, options?: any): Observable<any> {
        return this.http
            .put(this.REST_API_SERVER + url, data, options)
            .pipe(catchError(this.handleError.bind(this)));
        //.post(this.REST_API_SERVER + url, data, { observe: 'response' })
    }

    public delete(url: string, options?: any): Observable<any> {
        return this.http
            .delete(this.REST_API_SERVER + url, options)
            .pipe(catchError(this.handleError.bind(this)));
    }

    handleError(error: HttpErrorResponse) {
        console.log(error);
        let errorMessage = 'Unknown error!';
        if (error.status === 401) {
            this.router.navigate(['/sign-in']);
        }
        //console.log(JSON.stringify(error));
        if (error.error instanceof ErrorEvent) {
            errorMessage = `Error: ${error.error.message}`;
            const initialState = {
                title: "Something Went Wrong!",
                message: "Please try aftersometime!",
            };
            this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
            this.bsModalRef.content.closeBtnName = 'Close';
        } else {
            if (typeof error.error.code !== 'undefined') {
                const initialState = {
                    title: error.error.code,
                    message: error.error.message
                };
                this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
                this.bsModalRef.content.closeBtnName = 'Close';
            } else {
                const initialState = {
                    title: "Something Went Wrong!",
                    message: "Please try aftersometime!",
                };
                this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
                this.bsModalRef.content.closeBtnName = 'Close';
            }
        }
        //window.alert(errorMessage);
        return throwError(errorMessage);
    }
}