import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { EntityType } from '../model/enumerator/EntityType';
import { MediaType } from '../model/enumerator/MediaType';
import { MediaFile } from '../model/MediaFile';

@Injectable({
    providedIn: 'root' // just before your class
})
export class MediaService {

    constructor(private http: ApiHttpService) { }

    saveMediaService(entityId: number, entityType: EntityType, mediaType: MediaType, chatRoomId: string, formData: FormData, request?: any): Observable<any> {
        return this.http.post('/v1/media/upload-media/' + entityId + '/' + EntityType[entityType] + '/' + MediaType[mediaType] + '/' + chatRoomId, formData, request);
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
