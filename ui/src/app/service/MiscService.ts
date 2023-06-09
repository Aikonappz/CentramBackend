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
import { MapDLVO, MapDLVOList } from '../model/MapDLVO';
import { DistributionList, DistributionListList } from '../model/DistributionList';
import { Module } from '../model/Module';
import { Vendor, VendorList } from '../model/Vendor';
import { AssetModel } from '../model/AssetModel';
import { PermissionDTO } from '../model/PermissionDTO';
import { ChatMessage } from '../model/ChatMessage';
import { Project, ProjectList } from '../model/Project';
import { ProjectAllocationDetail } from '../model/ProjectAllocationDetail';
import { ProjectDeallocateDTO } from '../model/ProjectDeallocateDTO';
import { Holiday } from '../model/Holiday';

@Injectable({
    providedIn: 'root' // just before your class
})
export class MiscService {
    constructor(private http: ApiHttpService) { }
    requestDemoService(requestDemo: RequestDemoDTO): Observable<CommonResponse> {
        return this.http.post('/v1/misc/request-demo', requestDemo);
    }
    rolesService(request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-role', { "params": request });
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
    holidayCalenderHolidaysService(locationId: number, year: string, request?: any): Observable<Holiday[]> {
        return this.http.get('/v1/misc/holiday-callender/' + locationId + '/' + year, { "params": request });
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
    savePermissionService(notifications: Notification[], request?: any): Observable<any> {
        return this.http.post('/v1/misc/notification', notifications, { "params": request });
    }
    updateNotificationsStatusService(ids: number[], status: Status, request?: any): Observable<any> {
        return this.http.get('/v1/misc/notification/' + ids.join(",") + '/' + Status[status], { "params": request });
    }
    saveDistributionListService(mapdl: DistributionList): Observable<DistributionList> {
        return this.http.post('/v1/misc/distribution-list', mapdl);
    }
    distributionListsService(request?: any): Observable<DistributionListList> {
        return this.http.get('/v1/misc/all-distribution-list', { "params": request });
    }
    distributionListService(id: number, request?: any): Observable<DistributionList> {
        return this.http.get('/v1/misc/distribution-list/' + id, { "params": request });
    }
    modulesService(request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-module', { "params": request });
    }
    getModulesByRole(idRole: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-module-by-role/' + idRole, { "params": request });
    }
    getActionsByRoleAndModule(idRole: number, idModule: number, request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-action-by-role-module/' + idRole + '/' + idModule, { "params": request });
    }
    assetModelsService(request?: any): Observable<AssetModel> {
        return this.http.get('/v1/misc/all-asset-model', { "params": request });
    }
    saveVendorService(vendor: Vendor): Observable<Vendor> {
        return this.http.post('/v1/misc/vendor', vendor);
    }
    vendorsService(request?: any): Observable<VendorList> {
        return this.http.get('/v1/misc/all-vendor', { "params": request });
    }
    vendorService(id: number, request?: any): Observable<Vendor> {
        return this.http.get('/v1/misc/vendor/' + id, { "params": request });
    }
    saveProjectService(project: Project): Observable<Vendor> {
        return this.http.post('/v1/misc/project', project);
    }
    projectsService(request?: any): Observable<ProjectList> {
        return this.http.get('/v1/misc/all-project', { "params": request });
    }
    projectService(id: number, request?: any): Observable<Project> {
        return this.http.get('/v1/misc/project/' + id, { "params": request });
    }
    permissionService(permissionDTO: PermissionDTO): Observable<any> {
        return this.http.post('/v1/misc/permission', permissionDTO,);
    }
    actionsService(request?: any): Observable<any> {
        return this.http.get('/v1/misc/all-action', { "params": request });
    }
    startChatService(chatMessage: ChatMessage): Observable<any> {
        return this.http.post('/v1/misc/chat-message', chatMessage,);
    }
    agentSideInitiateChatService(chatRoomId: string, request?: any): Observable<any> {
        return this.http.put('/v1/misc/chat-message/action/' + chatRoomId, { "params": request });
    }
    closeChatService(chatRoomId: string, request?: any): Observable<any> {
        return this.http.put('/v1/misc/chat-message/close/' + chatRoomId, { "params": request });
    }
    chatMassagesService(chatRoomId: string, request?: any): Observable<any> {
        return this.http.get('/v1/misc/chat-message/' + chatRoomId, { "params": request });
    }
    allocateProjects(projectAllocationDetailList: ProjectAllocationDetail[]): Observable<any> {
        return this.http.post('/v1/misc/allocate-project', projectAllocationDetailList,);
    }
    deallocateProjects(projectDeallocateDTO: ProjectDeallocateDTO): Observable<any> {
        return this.http.post('/v1/misc/deallocate-project', projectDeallocateDTO,);
    }
}