import { IncidentTicketAllocationType } from "./enumerator/IncidentTicketAllocationType";

export class Setting {
    ticketAllocationType: IncidentTicketAllocationType;
    incidentPrefix: string;
    assetPrefix: string;

    constructor() {
        this.incidentPrefix = '';
        this.assetPrefix = '';
        this.ticketAllocationType = IncidentTicketAllocationType.GENERIC;
    }
}