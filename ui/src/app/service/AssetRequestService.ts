import { Injectable } from '@angular/core';
import { Observable, } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { AssetApprovalDTO } from '../model/AssetApprovalDTO';
import { Asset, } from '../model/Asset';
import { AssetRequest, AssetRequestList } from '../model/AssetRequest';

@Injectable({
    providedIn: 'root' // just before your class
})
export class AssetRequestService {
    constructor(private http: ApiHttpService) { }
    saveAssetRequest(asset: AssetRequest): Observable<AssetRequest> {
        return this.http.post('/v1/asset-request/', asset);
    }
    approveAssetRequest(assetApprovalDTO: AssetApprovalDTO): Observable<AssetRequest> {
        return this.http.put('/v1/asset-request/', assetApprovalDTO);
    }
    assetRequestsService(request?: any): Observable<AssetRequestList> {
        return this.http.get('/v1/asset-request/all', { "params": request });
    }
    assetRequestService(id: number, request?: any): Observable<AssetRequest> {
        return this.http.get('/v1/asset-request/' + id, { "params": request });
    }
}
