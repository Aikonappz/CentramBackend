import { Account } from "./Account";
import { Base } from "./Base";
import { Status } from "./enumerator/Status";
import { Organisation } from "./Organisation";

export class LocationVO extends Base {
    id: number;
    country: string;
    state: string;
    city: string;
    timezone: string;
    name: string;
    officeName: string;
    opsStartTime: string;
    opsEndTime: string;
    status: any;
    organisation: Organisation;
    account: Account;
    constructor() {
        super();
        this.id = null;
        this.country = '';
        this.state = '';
        this.city = '';
        this.timezone = '';
        this.name = '';
        this.opsStartTime = '';
        this.opsEndTime = '';
        this.status = Status.ACTIVE;
        this.organisation = new Organisation();
        this.account = new Account();
    }
}
export interface LocationList {
    content: LocationVO[];
    totalElements: number;
}