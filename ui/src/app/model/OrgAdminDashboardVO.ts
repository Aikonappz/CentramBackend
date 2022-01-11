import { IncidentStatusVO } from "./IncidentStatusVO";

export class OrgAdminDashboardVO {
    activeEmployees: number;
    inHouseVendors: number;
    outSourcedVendors: number;
    incidents: IncidentStatusVO[];

    financeApplications: number;
    hrApplications: number;
    hrQueries: number;
    itInfrastructure: number;
    personalComputing: number;
    salesApplications: number;
    softwareRequests: number;
    vehiclePass: number;

    constructor() {
        this.activeEmployees = -1;
        this.inHouseVendors = -1;
        this.outSourcedVendors = -1;
        this.incidents = [];
        this.financeApplications = -1;
        this.hrApplications = -1;
        this.hrQueries = -1;
        this.itInfrastructure = -1;
        this.personalComputing = -1;
        this.salesApplications = -1;
        this.softwareRequests = -1;
        this.vehiclePass = -1;
    }
}
