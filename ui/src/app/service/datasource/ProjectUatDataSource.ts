import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { ProjectUatService } from '../ProjectUatService';
import { ProjectUat, ProjectUatList } from '../../model/ProjectUat';


export class ProjectUatDataSource implements DataSource<ProjectUat>{

    private subject = new BehaviorSubject<ProjectUat[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();
    public data: ProjectUat[] = [];

    constructor(private service: ProjectUatService) { }

    connect(collectionViewer: CollectionViewer): Observable<ProjectUat[]> {
        return this.subject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.subject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    load(pageNumber = 0, pageSize = 10, req: Object = {}) {
        this.loadingSubject.next(true);
        let defaultParam = { page: pageNumber, size: pageSize, sort: "id,asc", };
        let params = Object.assign(
            req,
            defaultParam
        );
        //console.log(params);
        this.service.getUploadedUatScripts(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: ProjectUatList) => {
                this.subject.next(result.content);
                this.data = result.content;
                this.countSubject.next(result.totalElements);
            });
    }

}