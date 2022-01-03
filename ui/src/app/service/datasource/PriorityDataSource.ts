

import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";
import { MiscService } from '../MiscService';
import { Priority, PriorityList } from '../../model/Priority';


export class PriorityDataSource implements DataSource<Priority>{

    private objSubject = new BehaviorSubject<Priority[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: MiscService) { }

    connect(collectionViewer: CollectionViewer): Observable<Priority[]> {
        return this.objSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.objSubject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    loadData(pageNumber = 0, pageSize = 10, req = {}) {
        this.loadingSubject.next(true);
        let defaultParam = {
            page: pageNumber, size: pageSize, sort: "name,asc"
        };
        let params = Object.assign(
            req,
            defaultParam
        );
        this.service.prioritiesService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: PriorityList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}