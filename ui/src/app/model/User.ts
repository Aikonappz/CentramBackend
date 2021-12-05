import { Base } from "./Base";
import { Department } from "./Department";
import { Status } from "./enumerator/Status";
import { LocationVO } from "./LocationVO";
import { Organisation } from "./Organisation";

export class User extends Base {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    contactNo: string;
    secContactNo: string;
    employeeId: string;
    managerId: string;
    projectCode: string;
    roles: number[];
    status: any;
    location: LocationVO;
    department: Department;
    organisation: Organisation;

    constructor() {
        super();
        this.id = null;
        this.firstName = '';
        this.lastName = '';;
        this.email = '';
        this.password = '';
        this.contactNo = '';
        this.secContactNo = '';
        this.employeeId = '';
        this.managerId = '';
        this.projectCode = '';
        this.roles = [];
        this.status = Status.ACTIVE;
        this.location = new LocationVO();
        this.department = new Department();
        this.organisation = new Organisation();
    }

}