import { TicketAllocationType } from "./enumerator/TicketAllocationType";

export class Setting {
    ticketAllocationType: TicketAllocationType;
    incidentPrefix: string;
    assetPrefix: string;

    constructor() {
        this.incidentPrefix = '';
        this.assetPrefix = '';
        this.ticketAllocationType = TicketAllocationType.GENERIC;
    }
}