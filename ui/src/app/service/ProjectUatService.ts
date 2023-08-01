import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { ProjectUat } from '../model/ProjectUat';
import { ProjectUatScript } from '../model/ProjectUatScript';
import { ProjectUatScriptDetail, ProjectUatScriptDetailList } from '../model/ProjectUatScriptDetail';


@Injectable({
    providedIn: 'root' // just before your class
})
export class ProjectUatService {
    constructor(private http: ApiHttpService) { }
    uploadProjectUatScript(formData: FormData, request?: any): Observable<ProjectUat> {
        return this.http.post('/v1/project-uat/upload-scripts', formData, request);
    }
    getProjectUatScripts(request?: any): Observable<ProjectUatScript[]> {
        return this.http.get('/v1/project-uat/uat-script', { "params": request });
    }
    getProjectUatScriptDetails(request?: any): Observable<ProjectUatScriptDetailList> {
        return this.http.get('/v1/project-uat/uat-script-detail', { "params": request });
    }
    saveProjectUatScriptDetail(projectUatScriptDetail: ProjectUatScriptDetail, request?: any): Observable<any> {
        return this.http.post('/v1/project-uat/update-project-uat-script-detail', projectUatScriptDetail, { "params": request });
    }
    markProjectUatComplate(uatScriptId: number, request?: any): Observable<ProjectUatScript> {
        return this.http.put('/v1/project-uat/mark-uat-complete/' + uatScriptId, { "params": request });
    }
}