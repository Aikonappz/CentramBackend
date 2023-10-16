import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { ProjectUatScriptDetail, ProjectUatScriptDetailList } from '../../model/ProjectUatScriptDetail';
import { ProjectUatService } from '../ProjectUatService';
import { ProjectUatScript, ProjectUatScriptList } from '../../model/ProjectUatScript';


export class ProjectUatScriptDataSource implements DataSource<ProjectUatScript>{

    private subject = new BehaviorSubject<ProjectUatScript[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();
    public data: ProjectUatScript[] = [];

    constructor(private service: ProjectUatService) { }

    connect(collectionViewer: CollectionViewer): Observable<ProjectUatScript[]> {
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
        this.service.getAllProjectUatScripts(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: ProjectUatScriptList) => {
                this.subject.next(result.content);
                this.data = result.content;
                this.countSubject.next(result.totalElements);
            });
    }

}