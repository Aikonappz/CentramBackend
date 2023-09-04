import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { ProjectUat, ProjectUatList } from '../model/ProjectUat';
import { ProjectUatScript, ProjectUatScriptList } from '../model/ProjectUatScript';
import { ProjectUatScriptDetail, ProjectUatScriptDetailList } from '../model/ProjectUatScriptDetail';


@Injectable({
    providedIn: 'root' // just before your class
})
export class ProjectUatService {
    constructor(private http: ApiHttpService) { }
    uploadProjectUatScript(formData: FormData, request?: any): Observable<ProjectUat> {
        return this.http.post('/v1/project-uat/upload-script', formData, request);
    }
    getProjectUats(request?: any): Observable<ProjectUat[]> {
        return this.http.get('/v1/project-uat/uat-cycles', { "params": request });
    }
    getProjectUatScripts(request?: any): Observable<ProjectUatScript[]> {
        return this.http.get('/v1/project-uat/uat-scripts', { "params": request });
    }
    getProjectUatScriptDetails(request?: any): Observable<ProjectUatScriptDetailList> {
        if (request.projectUATScriptId == null || typeof request.projectUATScriptId == 'undefined') {
            request.projectUATScriptId = -1;
        }
        return this.http.get('/v1/project-uat/uat-script-detail', { "params": request });
    }
    saveProjectUatScriptDetail(projectUatScriptDetail: ProjectUatScriptDetail, request?: any): Observable<any> {
        return this.http.post('/v1/project-uat/update-project-uat-script-detail', projectUatScriptDetail, { "params": request });
    }
    markProjectUatScriptComplete(uatScriptId: number, request?: any): Observable<ProjectUatScript> {
        return this.http.put('/v1/project-uat/mark-project-uat-script-complete/' + uatScriptId, { "params": request });
    }
    getAllProjectUatScripts(request?: any): Observable<ProjectUatScriptList> {
        return this.http.get('/v1/project-uat/uat-script', { "params": request });
    }
    markUATCycleComplete(projectUatId: number, request?: any): Observable<ProjectUat> {
        return this.http.put('/v1/project-uat/mark-project-uat-complete/' + projectUatId, { "params": request });
    }
    getUploadedUatScripts(request?: any): Observable<ProjectUatList> {
        return this.http.get('/v1/project-uat/uploaded-scripts', { "params": request });
    }
}