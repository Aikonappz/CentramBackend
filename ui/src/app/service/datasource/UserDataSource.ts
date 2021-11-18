

import { DataSource } from '@angular/cdk/table';

import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";

import { catchError, finalize } from "rxjs/operators";
import { UserVO, UserVOListResponse } from '../../model/UserVO';
import { UserService } from '../UserService';


export class UserDataSource implements DataSource<UserVO>{

    private userVOSubject = new BehaviorSubject<UserVO[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private countSubject = new BehaviorSubject<number>(0);
    public counter$ = this.countSubject.asObservable();

    constructor(private userService: UserService) { }

    connect(collectionViewer: CollectionViewer): Observable<UserVO[]> {
        return this.userVOSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.userVOSubject.complete();
        this.loadingSubject.complete();
        this.countSubject.complete();
    }

    loadUserVOs(pageNumber = 0, pageSize = 10, req = {}) {
        this.loadingSubject.next(true);
        let defaultParam = { page: pageNumber, size: pageSize };
        let params = Object.assign(
            req,
            defaultParam
        );
        //console.log(params);
        this.userService.getUsersService(params)
            .pipe(
                catchError(() => of([])),
                finalize(() => this.loadingSubject.next(false))
            )
            .subscribe((result: UserVOListResponse) => {
                this.userVOSubject.next(result.content);
                this.countSubject.next(result.totalElements);
            });
    }

}