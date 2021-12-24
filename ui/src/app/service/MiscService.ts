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

@Injectable({
    providedIn: 'root' // just before your class
})
export class MiscService {
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
    departmentsService(request?: any): Observable<DepartmentList> {
        return this.http.get('/v1/misc/all-department', { "params": request });
    }
    departmentService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/department/' + id, { "params": request });
    }
    updateDepartmentsStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.put('/v1/misc/department/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
    saveDepartmentService(dept: Department): Observable<Department> {
        return this.http.post('/v1/misc/department', dept);
    }
    updateLocationsStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.put('/v1/misc/location/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
    saveLocationService(loc: LocationVO): Observable<LocationVO> {
        return this.http.post('/v1/misc/location', loc);
    }
    locationsService(request?: any): Observable<LocationList> {
        return this.http.get('/v1/misc/all-location', { "params": request });
    }
    locationService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/location/' + id, { "params": request });
    }
    updatePrioritiesStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.put('/v1/misc/priority/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
    savePriorityService(prty: Priority): Observable<LocationVO> {
        return this.http.post('/v1/misc/priority', prty);
    }
    prioritiesService(request?: any): Observable<PriorityList> {
        return this.http.get('/v1/misc/all-priority', { "params": request });
    }
    priorityService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/priority/' + id, { "params": request });
    }
    holidayCalendersService(request?: any): Observable<HolidayCalenderList> {
        return this.http.get('/v1/misc/all-holiday-callender', { "params": request });
    }
    holidayCalenderService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/holiday-callender/' + id, { "params": request });
    }
    saveHolidayCalenderService(formData: FormData, request?: any): Observable<Department> {
        return this.http.post('/v1/misc/upload-holiday-calender', formData, request);
    }
    downloadholidayCalenderService(id: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/holiday-callender/' + id + '/download', { responseType: 'blob' });
    }

    notificationsService(request?: any): Observable<NotificationList> {
        return this.http.get('/v1/misc/all-notifications', { "params": request });
    }

    notificationService(id: number, request?: any): Observable<Notification> {
        return this.http.get('/v1/misc/notification/' + id, { "params": request });
    }

    saveNotificationService(notifications: Notification[], request?: any): Observable<any> {
        return this.http.post('/v1/misc/notification', notifications, { "params": request });
    }

    updateNotificationsStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.get('/v1/misc/notification/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
}
