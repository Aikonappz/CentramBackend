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
    private authRequest: AuthRequest;

    constructor(private http: ApiHttpService) { }

    requestForgotPasswordService(authRequest: AuthRequest): Observable<CommonResponse> {
        return this.http.post('/v1/user/forgot-password', authRequest);
    }

    resetPasswordService(authRequest: AuthRequest): Observable<CommonResponse> {
        return this.http.post('/v1/user/reset-password', authRequest);
    }

    signInService(authRequest: AuthRequest): Observable<any> {
        return this.http.post('/v1/user/sign-in', authRequest);
    }

    signOutService(): Observable<any> {
        return this.http.get('/v1/user/sign-out');
    }

    getUsersService(request?: any): Observable<UserVOListResponse> {
        return this.http.get('/v1/user/all', { "params": request });
    }

    updateStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.get('/v1/user/' + ids.join(",") + '/' + Status[status], { "params": request });
    }

    addUserService(user: User): Observable<UserVO> {
        return this.http.post('/v1/user/', user);
    }

    editUserService(user: User): Observable<UserVO> {
        return this.http.put('/v1/user/', user);
    }

    getUserService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/user/' + id, { "params": request });
    }

    changePasswordService(user: UserDTO, request?: any): Observable<any> {
        return this.http.put('/v1/user/change-password', user, { "params": request });
    }

    downloadUsersService(request?: any): Observable<any> {
        return this.http.get('/v1/user/download', { responseType: 'blob' });
    }

    uploadUsersService(formData: FormData, request?: any): Observable<any> {
        return this.http.post('/v1/user/upload-users', formData);
    }

    //   getAllCompany(): Observable<Company[]> {
    //     return this.http.get(this.getAllCompanyList);
    //   }
    //   registerCompany(company: Company): Observable<Company> {
    //     return this.http.post(this.companyRegister, company);
    //   }
    //   deleteCompany(companyCode: string): Observable<any> {
    //     return this.http.delete(this.companyDelete + companyCode);
    //   }
    //   getCompanyInfo(companyCode: string): Observable<Company> {
    //     return this.http.get(this.companyInfo + companyCode);
    //   }
    //   // Stock Services
    //   getStockByCompanyAndDate(
    //     companyCode: string,
    //     startDate: string,
    //     endDate: string
    //   ): Observable<StockDetail> {
    //     return this.http.get(
    //       this.stockByCompany + companyCode + '/' + startDate + '/' + endDate
    //     );
    //   }
    //   getStockByCompanyCode(companyCode: string): Observable<Stock[]> {
    //     return this.http.get(this.stockByCompany + companyCode + '/latest');
    //   }
    //   addStockToCompany(companyCode: string, s: Stock): Observable<Stock> {
    //     return this.http.post(this.addStock + companyCode, s);
    //   }
    //   setCompanyCodeForFetchingStock(company: Company) {
    //     return (this.companyToFetchStock = company);
    //   }
    //   getCompanyCodeForFetchingStock() {
    //     return this.companyToFetchStock;
    //   }
}
