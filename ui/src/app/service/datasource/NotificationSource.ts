import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";

import { MiscService } from '../MiscService';
import { Notification, NotificationList } from '../../model/Notification';


export class NotificationDataSource implements DataSource<Notification>{

    private subject = new BehaviorSubject<Notification[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: MiscService) { }

    connect(collectionViewer: CollectionViewer): Observable<Notification[]> {
        return this.subject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.subject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    load(pageNumber = 0, pageSize = 10, req = {}) {
        this.loadingSubject.next(true);
        let defaultParam = { page: pageNumber, size: pageSize, sort: "id,desc", };
        let params = Object.assign(
            req,
            defaultParam
        );
        //console.log(params);
        this.service.notificationsService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: NotificationList) => {
                this.subject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}