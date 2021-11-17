

import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";
import { UserVO, UserVOListResponse } from '../../model/UserVO';
import { UserService } from '../UserService';
import { Organisation, OrganisationList } from '../../model/Organisation';
import { OrganisationService } from '../OrganisationService';


export class OrganisationDataSource implements DataSource<Organisation>{

    private objSubject = new BehaviorSubject<Organisation[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: OrganisationService) { }

    connect(collectionViewer: CollectionViewer): Observable<Organisation[]> {
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
        //console.log(params);
        this.service.getOrganisationsService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: OrganisationList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}