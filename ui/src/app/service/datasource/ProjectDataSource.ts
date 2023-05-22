

import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";
import { MiscService } from '../MiscService';
import { Vendor, VendorList } from '../../model/Vendor';
import { Project, ProjectList } from '../../model/Project';


export class ProjectDataSource implements DataSource<Project>{

    private objSubject = new BehaviorSubject<Project[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: MiscService) { }

    connect(collectionViewer: CollectionViewer): Observable<Project[]> {
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
        this.service.projectsService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: ProjectList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }
}