import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class SessionStorageService {
    private type: string;
    private storage: any;

    public constructor(type: string) {
        this.type = type;
        if (this.type == "LOCAL") {
            this.storage = localStorage;
        } else if (this.type == "SESSION") {
            this.storage = sessionStorage;
        }
    }
    public set(key: string, value: any): void {
        this.storage.setItem(key, btoa(value));
    }
    public get(key: string): string {
        return atob(this.storage.getItem(key));
    }
    public remove(key: string): void {
        this.storage.removeItem(key);
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