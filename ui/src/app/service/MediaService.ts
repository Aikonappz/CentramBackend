import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { RequestDemoDTO } from '../model/RequestDemoDTO';
import { ApiHttpService } from './ApiHttpService';
import { CommonResponse } from '../model/CommonResponse';
import { LocationList, LocationVO } from '../model/LocationVO';
import { Department, DepartmentList } from '../model/Department';
import { Status } from '../model/enumerator/Status';
import { Priority, PriorityList } from '../model/Priority';
import { HolidayCalenderList } from '../model/HolidayCalender';
import { Notification, NotificationList } from '../model/Notification';
import { EntityType } from '../model/enumerator/EntityType';
import { MediaType } from '../model/enumerator/MediaType';
import { MediaFile } from '../model/MediaFile';

@Injectable({
    providedIn: 'root' // just before your class
})
export class MediaService {

    constructor(private http: ApiHttpService) { }

    saveMediaService(entityId: number, entityType: EntityType, mediaType: MediaType, formData: FormData, request?: any): Observable<any> {
        return this.http.post('/v1/media/upload-media/' + entityId + '/' + EntityType[entityType] + '/' + MediaType[mediaType], formData, request);
    }

    deleteMediaService(mediaId: number, request?: any): Observable<any> {
        return this.http.delete('/v1/media/' + mediaId, { "params": request });
    }

    getMediaService(mediaId: number, request?: any): Observable<MediaFile> {
        return this.http.get('/v1/media/' + mediaId, { "params": request });
    }

    downloadMediaService(mediaId: number, request?: any): Observable<any> {
        return this.http.get('/v1/media/' + mediaId + '/download', { responseType: 'blob' });
    }
}
