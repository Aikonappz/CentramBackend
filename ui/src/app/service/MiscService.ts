import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, from } from 'rxjs';
import { RequestDemoDTO } from '../model/RequestDemoDTO';
import { ApiHttpService } from './ApiHttpService';
import { CommonResponse } from '../model/CommonResponse';

@Injectable({
    providedIn: 'root' // just before your class
})
export class MiscService {
    private requestDemo: RequestDemoDTO;

    constructor(private http: ApiHttpService) { }

    requestDemoService(requestDemo: RequestDemoDTO): Observable<CommonResponse> {
        return this.http.post('/v1/misc/request-demo', requestDemo);
    }

    rolesService(request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-roles', { "params": request });
    }

    roleService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/role/' + id, { "params": request });
    }

    locationsService(request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-locations', { "params": request });
    }

    locationService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/location/' + id, { "params": request });
    }

    departmentsService(request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-departments', { "params": request });
    }

    departmentService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/department/' + id, { "params": request });
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
