import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, } from '@angular/common/http';
import { throwError, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AppSettings } from '../config/AppSettings';
import { Router } from '@angular/router';
@Injectable({
    providedIn: 'root'
})
export class ApiHttpService {

    private REST_API_SERVER = AppSettings.API_ENDPOINT;

    constructor(private http: HttpClient, private router: Router) { }

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
        if (error.error instanceof ErrorEvent) {
            errorMessage = `Error: ${error.error.message}`;
        } else {
            // Server-side errors
            errorMessage = `Error Code: ${error.error.code}\nMessage: ${error.error.message}`;
        }
        window.alert(errorMessage);
        return throwError(errorMessage);
    }
}