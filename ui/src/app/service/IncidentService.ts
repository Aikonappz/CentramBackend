import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { Incident, IncidentList } from '../model/Incident';
import { AssetApprovalDTO } from '../model/AssetApprovalDTO';

@Injectable({
    providedIn: 'root' // just before your class
})
export class IncidentService {

    constructor(private http: ApiHttpService) { }

    saveIncidentService(inc: Incident): Observable<Incident> {
        return this.http.post('/v1/incident/', inc);
    }
    agentIncidentsService(request?: any): Observable<IncidentList> {
        return this.http.get('/v1/incident/agent', { "params": request });
    }
    userIncidentsService(request?: any): Observable<IncidentList> {
        return this.http.get('/v1/incident/user', { "params": request });
    }
    userAllocatedAssetsService(request?: any): Observable<IncidentList> {
        return this.http.get('/v1/incident/allocated-assets', { "params": request });
    }
    incidentService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/incident/' + id, { "params": request });
    }
    assignIncidentService(ids: number[], userId: number, comment: string, request?: any): Observable<any> {
        return this.http.get('/v1/incident/assign/' + ids.join(",") + '/' + userId + '/' + comment, { "params": request });
    }
    reOpenIncidentService(ids: number[], status: string, request?: any): Observable<any> {
        return this.http.get('/v1/incident/reopen/' + ids.join(",") + '/' + status, { "params": request });
    }
    approveAssetRequest(assetApprovalDTO: AssetApprovalDTO): Observable<any> {
        return this.http.put('/v1/incident/asset/approval-action', assetApprovalDTO);
    }
    deallocateAssetRequest(allocateAssetDTO: any): Observable<any> {
        return this.http.put('/v1/incident/deallocate-asset', allocateAssetDTO);
    }
    pendingAssetApprovalService(request?: any): Observable<IncidentList> {
        return this.http.get('/v1/incident/asset/pending/approval', { "params": request });
    }
}
