export class LocalStorageService {
    constructor() { }
    public static set(key: string, value: any): void {
        localStorage.setItem(key, value);
    }
    public static get(key: string): string {
        return localStorage.getItem(key);
    }
    public static remove(key: string): void {
        localStorage.removeItem(key);
    }
    public static size(): number {
        return localStorage.length;
    }
    public static clear(): void {
        localStorage.clear();
    }
    public static key(index: number): string {
        return localStorage.key(index);
    }
}