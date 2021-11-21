import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
    providedIn: 'root' // just before your class
})
export class SpinnerService {

    private count = 0;
    private spinner$ = new BehaviorSubject<String>('');

    constructor() {
    }

    getSpinnerObserver(): Observable<String> {
        return this.spinner$.asObservable();
    }

    requestStarted() {
        if (++this.count === 1) {
            this.spinner$.next('start');
        }
    }

    requestedEnded() {
        if (--this.count === 0 || this.count === 0) {
            this.spinner$.next('end');
        }
    }

    resetSpinner() {
        this.count = 0;
        this.spinner$.next('stop');
    }
}