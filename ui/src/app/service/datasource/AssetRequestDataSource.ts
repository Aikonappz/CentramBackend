import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { AssetRequest, AssetRequestList } from '../../model/AssetRequest';
import { AssetRequestService } from '../AssetRequestService';


export class AssetRequestDataSource implements DataSource<AssetRequest>{

    private objSubject = new BehaviorSubject<AssetRequest[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: AssetRequestService) { }

    connect(collectionViewer: CollectionViewer): Observable<AssetRequest[]> {
        return this.objSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.objSubject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    loadData(pageNumber = 0, pageSize = 10, req = {}) {
        this.loadingSubject.next(true);
        let defaultParam = { page: pageNumber, size: pageSize };
        let params = Object.assign(
            req,
            defaultParam
        );
        this.service.assetRequestsService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: AssetRequestList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}