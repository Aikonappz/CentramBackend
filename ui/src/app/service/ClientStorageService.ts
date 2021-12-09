import { Injectable } from "@angular/core";
import { AppUtility } from "../config/AppUtility";

@Injectable({
    providedIn: 'root'
})
export class ClientStorageService {
    private storage: any;
    public constructor() {
        if (AppUtility.APP_CLIENT_STORAGE_TYPE == "LOCAL") {
            this.storage = localStorage;
        } else if (AppUtility.APP_CLIENT_STORAGE_TYPE == "SESSION") {
            this.storage = sessionStorage;
        } else {
            this.storage = localStorage;
        }
    }
    public set(key: string, value: any): void {
        this.storage.setItem(btoa(key), btoa(value));
    }
    public get(key: string): string {
        return atob(this.storage.getItem(btoa(key)));
    }
    public remove(key: string): void {
        this.storage.removeItem(btoa(key));
    }
    public clear(): void {
        this.storage.clear();
    }
    private key(index: number): string {
        return this.storage.key(index);
    }
    private size(): number {
        return this.storage.length;
    }
}