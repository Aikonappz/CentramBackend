import { IncidentModuleVO } from "./IncidentModuleVO";
import { IncidentStatusVO } from "./IncidentStatusVO";

export class OrgAdminDashboardVO {
    activeEmployees: number;
    inHouseVendors: number;
    outSourcedVendors: number;
    moduleWiseIncidents: IncidentModuleVO[];
    statusWiseIncidents: IncidentStatusVO[];

    constructor(data: any) {
        if (data == null) {
            this.activeEmployees = -1;
            this.inHouseVendors = -1;
            this.outSourcedVendors = -1;
            this.moduleWiseIncidents = [];
            this.statusWiseIncidents = [];
        } else {
            this.activeEmployees = data.activeEmployees;
            this.inHouseVendors = data.inHouseVendors;
            this.outSourcedVendors = data.outSourcedVendors;
            this.moduleWiseIncidents = data.moduleWiseIncidents;
            this.statusWiseIncidents = data.statusWiseIncidents;
        }
    }
}
