import { TicketAllocationType } from "./enumerator/TicketAllocationType";

export class Setting {
    ticketAllocationType: TicketAllocationType;
    incidentPrefix: string;
    assetPrefix: string;
    inboundAssetRequestPrefix: string;
    outboundAssetRequestPrefix: string;

    constructor() {
        this.incidentPrefix = '';
        this.assetPrefix = '';
        this.inboundAssetRequestPrefix = '';
        this.outboundAssetRequestPrefix = '';
        this.ticketAllocationType = TicketAllocationType.ROUND_ROBIN;
    }
}