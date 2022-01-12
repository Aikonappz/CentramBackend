import { IncidentStatusVO } from "./IncidentStatusVO";

export class UserDashboardVO {
    incidents: IncidentStatusVO[];
    financeApplications: number;
    hrApplications: number;
    hrQueries: number;
    itInfrastructure: number;
    personalComputing: number;
    salesApplications: number;
    softwareRequests: number;
    vehiclePass: number;

    constructor(data: any) {
        if (data == null) {
            this.incidents = [];
            this.financeApplications = -1;
            this.hrApplications = -1;
            this.hrQueries = -1;
            this.itInfrastructure = -1;
            this.personalComputing = -1;
            this.salesApplications = -1;
            this.softwareRequests = -1;
            this.vehiclePass = -1;
        } else {
            this.incidents = data.incidents;
            let incidents = data.incidents;
            for (let k in incidents) {
                if (incidents[k].status == 'FINANCE_APPLICATIONS') {
                    this.financeApplications = incidents[k].count;
                } else if (incidents[k].status == 'HR_APPLICATIONS') {
                    this.hrApplications = incidents[k].count;
                } else if (incidents[k].status == 'HR_QUERIES') {
                    this.hrQueries = incidents[k].count;
                } else if (incidents[k].status == 'IT_INFRASTRUCTURE') {
                    this.itInfrastructure = incidents[k].count;
                } else if (incidents[k].status == 'PERSONAL_COMPUTING') {
                    this.personalComputing = incidents[k].count;
                } else if (incidents[k].status == 'SALES_APPLICATIONS') {
                    this.salesApplications = incidents[k].count;
                } else if (incidents[k].status == 'SOFTWARE_REQUESTS') {
                    this.softwareRequests = incidents[k].count;
                } else if (incidents[k].status == 'VEHICLE_PASS') {
                    this.vehiclePass = incidents[k].count;
                }
            }
        }
    }
}
