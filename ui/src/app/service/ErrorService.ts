`import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
    providedIn: 'root' // just before your class
})
export class ErrorService {

    private count = 0;
    private error$ = new BehaviorSubject<String>('');

    constructor() {
    }

    getErrorObserver(): Observable<String> {
        return this.error$.asObservable();
    }

    errorStarted() {
        if (++this.count === 1) {
            this.error$.next('start');
        }
    }

    errorEnded() {
        if (--this.count === 0 || this.count === 0) {
            this.error$.next('end');
        }
    }

    errorSpinner() {
        this.count = 0;
        this.error$.next('stop');
    }
}