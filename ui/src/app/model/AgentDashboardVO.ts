import { IncidentPriorityVO } from "./IncidentPriorityVO";
import { IncidentModuleVO } from "./IncidentModuleVO";
import { IncidentStatusVO } from "./IncidentStatusVO";
export class AgentDashboardVO {
    priorityWiseIncidents: IncidentPriorityVO[];
    moduleWiseIncidents: IncidentModuleVO[];
    statusWiseIncidents: IncidentStatusVO[];
    constructor(data: any) {
        if (data == null) {
            this.moduleWiseIncidents = [];
            this.priorityWiseIncidents = [];
            this.statusWiseIncidents = [];
        } else {
            this.moduleWiseIncidents = data.moduleWiseIncidents;
            this.priorityWiseIncidents = data.priorityWiseIncidents;
            this.statusWiseIncidents = data.statusWiseIncidents;
        }
    }
}
