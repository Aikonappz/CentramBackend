import { Base } from "./Base";
import { TicketAllocationType } from "./enumerator/TicketAllocationType";
import { Organisation } from "./Organisation";
import { VendorModule } from "./VendorModule";

export class Account extends Base {
    id: number;
    label: string;
    name: string;
    accountNo: string;
    contactName: string;
    contactEmail: string;
    contactNumber: string;
    contactAddress: string;
    status: any;
    organisation: Organisation;
    ticketAllocationType: TicketAllocationType;
    accountType: any;

    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.accountNo = '';
        this.contactName = '';
        this.contactEmail = '';
        this.contactNumber = '';
        this.contactAddress = '';
        this.status = 'ACTIVE';
        this.organisation = new Organisation();
        this.ticketAllocationType = TicketAllocationType.GENERIC;
    }
}

export interface AccountList {
    content: Account[];
    totalElements: number;
}