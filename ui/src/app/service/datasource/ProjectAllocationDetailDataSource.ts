import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { AssetOrder, AssetOrderList } from '../../model/AssetOrder';
import { ReportService } from '../ReportService';
import { UatScriptReportDTO, UatScriptReportDTOList } from '../../model/UatScriptReportDTO';
import { AllocationDetailVO, AllocationDetailVOList } from '../../model/AllocationDetailVO';
import { MiscService } from '../MiscService';


export class ProjectAllocationDetailDataSource implements DataSource<AllocationDetailVO>{

    private objSubject = new BehaviorSubject<AllocationDetailVO[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: MiscService) { }

    connect(collectionViewer: CollectionViewer): Observable<AllocationDetailVO[]> {
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
        this.service.allocationDetailsService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: AllocationDetailVOList) => {
                this.objSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}