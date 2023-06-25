import { Account } from "./Account";
import { Base } from "./Base";
import { Status } from "./enumerator/Status";
import { Organisation } from "./Organisation";

export class Priority extends Base {
    id: number;
    name: string;
    description: string;
    sla: string;
    status: any;
    priorityType: any;
    organisation: Organisation;
    account: Account;
    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.description = '';
        this.sla = '';
        this.status = Status.ACTIVE;
        this.organisation = new Organisation();
        this.account = new Account();
        this.priorityType = null;
    }
}

export interface PriorityList {
    content: Priority[];
    totalElements: number;
}