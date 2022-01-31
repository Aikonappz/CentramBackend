import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, } from '@angular/common/http';
import { throwError, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import { ErrormessageComponent } from '../views/errormessage/errormessage.component';
import { environment } from '../../environments/environment';
import { Location } from '@angular/common';
@Injectable({
    providedIn: 'root'
})
export class ApiHttpService {
    public bsModalRef: BsModalRef;
    private REST_API_SERVER = environment.appServiceEndpoint;

    constructor(
        private _location: Location,
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
            this._location.back();
            //this.router.navigate(['/sign-in']);
        }
        //console.log(JSON.stringify(error));
        if (error.error instanceof ErrorEvent) {
            let err = error;
            if (typeof err.error.code !== 'undefined' && err.error.code == 'PROFILE_INACTIVE') {
                const initialState = {
                    title: err.error.code,
                    message: err.error.message
                };
                this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
            } else {
                const initialState = {
                    title: "Something Went Wrong!",
                    message: "Please try after sometime!",
                };
                this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
            }
        } else {
            if (typeof error.error.code !== 'undefined') {
                const initialState = {
                    title: error.error.code,
                    message: error.error.message
                };
                this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
            } else {
                const initialState = {
                    title: "Something Went Wrong!",
                    message: "Please try after sometime!",
                };
                this.bsModalRef = this.modalService.show(ErrormessageComponent, { initialState });
            }
        }
        //window.alert(errorMessage);
        return throwError(errorMessage);
    }
}