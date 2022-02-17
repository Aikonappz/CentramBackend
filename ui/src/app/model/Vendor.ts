import { Base } from "./Base";
import { TicketAllocationType } from "./enumerator/TicketAllocationType";
import { Organisation } from "./Organisation";
import { VendorModule } from "./VendorModule";

export class Vendor extends Base {
    id: number;
    name: string;
    contactName: string;
    contactEmail: string;
    contactNumber: string;
    contactAddress: string;
    status: any;
    inHouse: boolean;
    vendorModules: VendorModule[];
    organisation: Organisation;
    ticketAllocationType: TicketAllocationType;
    vendorType : any;
    
    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.contactName = '';
        this.contactEmail = '';
        this.contactNumber = '';
        this.contactAddress = '';
        this.status = 'ACTIVE';
        this.vendorModules = [];
        this.organisation = new Organisation();
        this.ticketAllocationType = TicketAllocationType.GENERIC;
        this.inHouse = true;
    }
}

export interface VendorList {
    content: Vendor[];
    totalElements: number;
}