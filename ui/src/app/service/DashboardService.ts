import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { AdminDashboardVO } from '../model/AdminDashboardVO';
import { OrgAdminDashboardVO } from '../model/OrgAdminDashboardVO';
import { UserDashboardVO } from '../model/UserDashboardVO';
import { AgentDashboardVO } from '../model/AgentDashboardVO';
import { CategoryAdminDashboardVO } from '../model/CategoryAdminDashboardVO';

@Injectable({
    providedIn: 'root' // just before your class
})
export class DashboardService {
    constructor(private http: ApiHttpService) { }
    appAdminDashboard(request?: any): Observable<AdminDashboardVO> {
        return this.http.get('/v1/dashboard/app-admin', { "params": request });
    }
    orgAdminDashboard(request?: any): Observable<OrgAdminDashboardVO> {
        return this.http.get('/v1/dashboard/org-admin', { "params": request });
    }
    userDashboard(request?: any): Observable<UserDashboardVO> {
        return this.http.get('/v1/dashboard/user', { "params": request });
    }
    agentDashboard(request?: any): Observable<AgentDashboardVO> {
        return this.http.get('/v1/dashboard/agent', { "params": request });
    }
    categoryAdminDashboard(request?: any): Observable<CategoryAdminDashboardVO> {
        return this.http.get('/v1/dashboard/category-admin', { "params": request });
    }
}
