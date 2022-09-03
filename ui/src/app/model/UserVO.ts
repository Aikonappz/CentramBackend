import { LicenseType } from "./enumerator/LicenseType";
import { Status } from "./enumerator/Status";

export class UserVO {

    id: number;
    firstName: string;
    lastName: string;
    fullName: string;
    nameWithId: string;
    email: string;
    password: string;
    contactNo: string;
    secContactNo: string;
    employeeId: string;
    projectCode: string;
    roles: number[];
    roleNames: string[];
    roleViewNames: string[];
    status: any;
    timeZone: string;
    managerId: number;
    organisationId: number;
    organisation: string;
    locationId: number;
    location: string;
    locationOfficeName: string;
    departmentId: number;
    department: string;
    licenseType: LicenseType;
    label: string;

    constructor() {
        this.id = null;
        this.firstName = '';
        this.lastName = '';
        this.email = '';
        this.password = '';
        this.contactNo = '';
        this.secContactNo = '';
        this.employeeId = '';
        this.projectCode = '';
        this.roles = [];
        this.roleNames = [];
        this.roleViewNames = [];
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