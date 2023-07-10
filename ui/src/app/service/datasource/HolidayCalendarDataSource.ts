import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";
import { MiscService } from '../MiscService';
import { HolidayCalendar, HolidayCalendarList,  } from '../../model/HolidayCalendar';


export class HolidayCalendarDataSource implements DataSource<HolidayCalendar>{

    private objSubject = new BehaviorSubject<HolidayCalendar[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: MiscService) { }

    connect(collectionViewer: CollectionViewer): Observable<HolidayCalendar[]> {
        return this.objSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.objSubject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    loadData(pageNumber = 0, pageSize = 10) {
        this.loadingSubject.next(true);
        this.service.holidayCalendarsService({ page: pageNumber, size: pageSize })
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: HolidayCalendarList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}