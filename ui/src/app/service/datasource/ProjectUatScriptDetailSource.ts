import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { ProjectUatScriptDetail, ProjectUatScriptDetailList } from '../../model/ProjectUatScriptDetail';
import { ProjectUatService } from '../ProjectUatService';


export class ProjectUatScriptDetailSource implements DataSource<ProjectUatScriptDetail>{

    private subject = new BehaviorSubject<ProjectUatScriptDetail[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private service: ProjectUatService) { }

    connect(collectionViewer: CollectionViewer): Observable<ProjectUatScriptDetail[]> {
        return this.subject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.subject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    load(pageNumber = 0, pageSize = 100, req: Object = {}) {
        this.loadingSubject.next(true);
        let defaultParam = { page: pageNumber, size: pageSize, sort: "id,asc", };
        let params = Object.assign(
            req,
            defaultParam
        );
        //console.log(params);
        this.service.getProjectUatScriptDetails(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: ProjectUatScriptDetailList) => {
                this.subject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}