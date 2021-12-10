import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { Incident, IncidentList } from '../model/Incident';

@Injectable({
    providedIn: 'root' // just before your class
})
export class IncidentService {

    constructor(private http: ApiHttpService) { }

    incomingIncidentsService(request?: any): Observable<IncidentList> {
        return this.http.get('/v1/incident/incoming-all', { "params": request });
    }

    incidentsService(request?: any): Observable<IncidentList> {
        return this.http.get('/v1/incident/all', { "params": request });
    }

    incidentService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/incident/' + id, { "params": request });
    }

    saveIncidentService(inc: Incident): Observable<Incident> {
        return this.http.post('/v1/incident/', inc);
    }

    assignIncidentService(ids: number[], userId: number, request?: any): Observable<any> {
        return this.http.get('/v1/incident/assign/' + ids.join(",") + '/' + userId, { "params": request });
    }

    changeIncidentStatusService(ids: number[], status: string, request?: any): Observable<any> {
        return this.http.get('/v1/incident/change-status/' + ids.join(",") + '/' + status, { "params": request });
    }
}
