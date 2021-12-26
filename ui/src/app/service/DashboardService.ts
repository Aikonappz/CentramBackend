import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { AdminDashboard } from '../model/AdminDashboard';

@Injectable({
    providedIn: 'root' // just before your class
})
export class DashboardService {
    constructor(private http: ApiHttpService) { }
    appAdminDashboard(request?: any): Observable<AdminDashboard> {
        return this.http.get('/v1/dashboard/app-admin', { "params": request });
    }
}
