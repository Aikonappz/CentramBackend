import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiHttpService } from './ApiHttpService';
import { CommonResponse } from '../model/CommonResponse';
import { AuthRequest } from '../model/AuthRequest';
import { Status } from '../model/enumerator/Status';
import { UserVO, UserVOListResponse } from '../model/UserVO';
import { User } from '../model/User';
import { UserDTO } from '../model/UserDTO';

@Injectable({
    providedIn: 'root' // just before your class
})
export class UserService {
    constructor(
        private http: ApiHttpService,
    ) { }
    signInService(authRequest: AuthRequest): Observable<any> {
        return this.http.post('/v1/user/sign-in', authRequest);
    }
    ssoSignInService(authRequest: AuthRequest): Observable<any> {
        return this.http.post('/v1/user/sso-sign-in', authRequest);
    }
    signOutService(): Observable<any> {
        return this.http.get('/v1/user/sign-out');
    }
    requestForgotPasswordService(authRequest: AuthRequest): Observable<CommonResponse> {
        return this.http.post('/v1/user/forgot-password', authRequest);
    }
    resetPasswordService(authRequest: AuthRequest): Observable<CommonResponse> {
        return this.http.post('/v1/user/reset-password', authRequest);
    }
    changePasswordService(user: UserDTO, request?: any): Observable<any> {
        return this.http.put('/v1/user/change-password', user, { "params": request });
    }
    getUsersService(request?: any): Observable<UserVOListResponse> {
        return this.http.get('/v1/user/all', { "params": request });
    }
    saveUserService(user: User): Observable<UserVO> {
        return this.http.post('/v1/user/', user);
    }
    updateStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.put('/v1/user/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
    getUserService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/user/' + id, { "params": request });
    }
    downloadUsersService(request?: any): Observable<any> {
        return this.http.get('/v1/user/download', { responseType: 'blob' });
    }
    uploadUsersService(formData: FormData, request?: any): Observable<any> {
        return this.http.post('/v1/user/upload', formData, request);
    }
    getUsersByModuleAndAction(request?: any): Observable<UserVO[]> {
        return this.http.get('/v1/user/find-by-modules-permissions', { "params": request });
    }
}
