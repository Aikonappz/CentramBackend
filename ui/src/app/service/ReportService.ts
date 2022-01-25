import { Injectable } from '@angular/core';
import { Observable, } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    constructor(private http: ApiHttpService) { }

    organisationReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/organisation', { "params": request });
    }
    downloadOrganisationReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/organisation/download', { "params": request, responseType: 'blob' });
    }
    incidentReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident', { "params": request });
    }
    downloadIncidentReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident/download', { "params": request, responseType: 'blob' });
    }

}
