import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { AssetApprovalDTO } from '../model/AssetApprovalDTO';
import { Asset, AssetList } from '../model/Asset';

@Injectable({
    providedIn: 'root' // just before your class
})
export class AssetService {
    constructor(private http: ApiHttpService) { }
    saveAsset(asset: Asset): Observable<Asset> {
        return this.http.post('/v1/asset/', asset);
    }
    approveAsset(assetApprovalDTO: AssetApprovalDTO): Observable<Asset> {
        return this.http.put('/v1/asset/', assetApprovalDTO);
    }
    assetsService(request?: any): Observable<AssetList> {
        return this.http.get('/v1/asset/all', { "params": request });
    }
    availableAssetsService(request?: any): Observable<AssetList> {
        return this.http.get('/v1/asset/available-asset', { "params": request });
    }
    assetService(id: number, request?: any): Observable<Asset> {
        return this.http.get('/v1/asset/' + id, { "params": request });
    }
    downloadAssetsService(request?: any): Observable<any> {
        return this.http.get('/v1/asset/download', { "params": request, responseType: 'blob' });
    }
    uploadAssetsService(formData: FormData, request?: any): Observable<any> {
        return this.http.post('/v1/asset/upload', formData, request);
    }
}
