

import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";
import { MiscService } from '../MiscService';
import { DistributionList, DistributionListList } from '../../model/DistributionList';
import { Vendor, VendorList } from '../../model/Vendor';


export class VendorDataSource implements DataSource<Vendor>{

    private objSubject = new BehaviorSubject<Vendor[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: MiscService) { }

    connect(collectionViewer: CollectionViewer): Observable<Vendor[]> {
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
        this.service.vendorsService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: VendorList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }
}