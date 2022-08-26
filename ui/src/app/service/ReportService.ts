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
    incidentEscalationReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident-escalation', { "params": request });
    }
    downloadIncidentEscalationReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident-escalation/download', { "params": request, responseType: 'blob' });
    }
    incidentReopenReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident-reopen', { "params": request });
    }
    downloadIncidentReopenReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident-reopen/download', { "params": request, responseType: 'blob' });
    }
    incidentAgingReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident-aging', { "params": request });
    }
    downloadIncidentAgingReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/incident-aging/download', { "params": request, responseType: 'blob' });
    }
    vendorReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/vendor', { "params": request });
    }
    downloadVendorReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/vendor/download', { "params": request, responseType: 'blob' });
    }
    orderReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/order', { "params": request });
    }
    downloadOrderReport(request?: any): Observable<any> {
        return this.http.get('/v1/report/order/download', { "params": request, responseType: 'blob' });
    }
}
