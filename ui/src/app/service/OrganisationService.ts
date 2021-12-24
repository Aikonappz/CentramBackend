import { Injectable } from '@angular/core';

import { Observable, } from 'rxjs';

import { ApiHttpService } from './ApiHttpService';

import { AuthRequest } from '../model/AuthRequest';

import { Status } from '../model/enumerator/Status';

import { Organisation } from '../model/Organisation';
import { Setting } from '../model/Setting';

@Injectable({
    providedIn: 'root' // just before your class
})
export class OrganisationService {
    constructor(private http: ApiHttpService) { }
    getOrganisationsService(request?: any): Observable<any> {
        return this.http.get('/v1/organisation/all', { "params": request });
    }
    updateStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.put('/v1/organisation/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
    saveOrganisationService(org: Organisation): Observable<Organisation> {
        return this.http.post('/v1/organisation/', org);
    }
    getOrganisationService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/organisation/' + id, { "params": request });
    }
    getOrganisationSettingService(request?: any): Observable<Setting> {
        return this.http.get('/v1/organisation/get-settings', { "params": request });
    }
    setOrganisationSettingService(org: Setting, request?: any): Observable<Setting> {
        return this.http.put('/v1/organisation/set-settings', org, request);
    }
}
