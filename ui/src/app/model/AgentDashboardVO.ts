import { IncidentPriorityVO } from "./IncidentPriorityVO";
import { IncidentStatusVO } from "./IncidentStatusVO";

export class AgentDashboardVO {
    priorityIncidents: IncidentPriorityVO[];
    statusIncidents: IncidentStatusVO[];

    p1: number;
    p2: number;
    p3: number;
    p4: number;
    p5: number;

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
            this.statusIncidents = [];
            this.financeApplications = -1;
            this.hrApplications = -1;
            this.hrQueries = -1;
            this.itInfrastructure = -1;
            this.personalComputing = -1;
            this.salesApplications = -1;
            this.softwareRequests = -1;
            this.vehiclePass = -1;
            this.p1 = -1;
            this.p2 = -1;
            this.p3 = -1;
            this.p4 = -1;
            this.p5 = -1;
        } else {
            this.statusIncidents = data.statusIncidents;
            this.priorityIncidents = data.priorityIncidents;
            for (let k in this.priorityIncidents) {
                if (this.priorityIncidents[k].priority.toLocaleLowerCase() == "p1") {
                    this.p1 = this.priorityIncidents[k].count;
                } else if (this.priorityIncidents[k].priority.toLocaleLowerCase() == "p2") {
                    this.p2 = this.priorityIncidents[k].count;
                } if (this.priorityIncidents[k].priority.toLocaleLowerCase() == "p3") {
                    this.p3 = this.priorityIncidents[k].count;
                } if (this.priorityIncidents[k].priority.toLocaleLowerCase() == "p4") {
                    this.p4 = this.priorityIncidents[k].count;
                } if (this.priorityIncidents[k].priority.toLocaleLowerCase() == "p5") {
                    this.p5 = this.priorityIncidents[k].count;
                }
            }
            let incidents = this.statusIncidents;
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
