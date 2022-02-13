import { IncidentModuleVO } from "./IncidentModuleVO";
import { IncidentStatusVO } from "./IncidentStatusVO";

export class UserDashboardVO {
    moduleWiseIncidents: IncidentModuleVO[];
    statusWiseIncidents: IncidentStatusVO[];
    constructor(data: any) {
        if (data == null) {
            this.moduleWiseIncidents = [];
            this.statusWiseIncidents = [];
        } else {
            this.moduleWiseIncidents = data.moduleWiseIncidents;
            this.statusWiseIncidents= data.statusWiseIncidents
        }
    }
}
