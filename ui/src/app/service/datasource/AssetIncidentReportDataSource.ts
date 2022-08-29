import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { Incident, IncidentList } from '../../model/Incident';
import { ReportService } from '../ReportService';


export class AssetIncidentReportDataSource implements DataSource<Incident>{

    private objSubject = new BehaviorSubject<Incident[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();
    public data: Incident[] = [];

    constructor(private service: ReportService) { }

    connect(collectionViewer: CollectionViewer): Observable<Incident[]> {
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
        this.service.assetReport(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: IncidentList) => {
                this.objSubject.next(result.content);
                this.data = result.content;
                this.countSubject.next(result.totalElements);
            });
    }

}