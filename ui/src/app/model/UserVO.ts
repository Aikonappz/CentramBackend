import { Status } from "./enumerator/Status";

export class UserVO {

    id: number;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    contactNo: string;
    employeeId: string;
    projectCode: string;
    roles: number[];
    roleNames: string[];
    status: Status;
    organisationId: number;
    locationId: number;
    departmentId: number;

    constructor() {
        this.id = null;
        this.firstName = '';
        this.lastName = '';
        this.email = '';
        this.password = '';
        this.contactNo = '';
        this.employeeId = '';
        this.projectCode = '';
        this.roles = [];
        this.roleNames = [];
        this.status = Status.ACTIVE;
        this.organisationId = null;
        this.locationId = null;
        this.departmentId = null;
    }

}

export interface UserVOListResponse {
    content: UserVO[];
    totalElements: number;
}