import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { AssetOrder, AssetOrderList } from '../model/AssetOrder';
import { AssetOrderApprovalDTO } from '../model/AssetOrderApprovalDTO';

@Injectable({
    providedIn: 'root' // just before your class
})
export class AssetOrderService {
    constructor(private http: ApiHttpService) { }
    saveAssetOrder(assetOrder: AssetOrder): Observable<AssetOrder> {
        return this.http.post('/v1/asset-order/', assetOrder);
    }
    approveAssetOrder(assetOrderApprovalDTO: AssetOrderApprovalDTO): Observable<AssetOrder> {
        return this.http.put('/v1/asset-order/', assetOrderApprovalDTO);
    }
    assetOrdersService(request?: any): Observable<AssetOrderList> {
        return this.http.get('/v1/asset-order/all', { "params": request });
    }
    assetOrderService(id: number, request?: any): Observable<AssetOrder> {
        return this.http.get('/v1/asset-order/' + id, { "params": request });
    }
}
