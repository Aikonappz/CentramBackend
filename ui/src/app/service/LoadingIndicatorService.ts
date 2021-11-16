import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class LoadingIndicatorService {

    private _loading: boolean = false;

    get loading(): boolean {
        return this._loading;
    }

    onRequestStarted(): void {
        this._loading = true;
    }

    onRequestFinished(): void {
        this._loading = false;
    }
}