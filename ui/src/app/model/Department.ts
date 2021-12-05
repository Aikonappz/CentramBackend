import { Base } from "./Base";
import { Status } from "./enumerator/Status";
import { Organisation } from "./Organisation";

export class Department extends Base {
    id: number;
    name: string;
    status: any;
    organisation: Organisation;
    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.status = Status.ACTIVE;
        this.organisation = new Organisation();
    }
}

export interface DepartmentList {
    content: Department[];
    totalElements: number;
}