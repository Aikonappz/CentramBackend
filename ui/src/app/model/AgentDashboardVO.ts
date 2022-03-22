import { IncidentPriorityVO } from "./IncidentPriorityVO";
import { IncidentModuleVO } from "./IncidentModuleVO";
import { IncidentStatusVO } from "./IncidentStatusVO";
export class AgentDashboardVO {
    priorityWiseIncidents: IncidentPriorityVO[];
    moduleWiseIncidents: IncidentModuleVO[];
    statusWiseIncidents: IncidentStatusVO[];
    priorityWiseAssetIncidents: IncidentPriorityVO[];
    moduleWiseAssetIncidents: IncidentModuleVO[];
    statusWiseAssetIncidents: IncidentStatusVO[];
    constructor(data: any) {
        if (data == null) {
            this.moduleWiseIncidents = [];
            this.priorityWiseIncidents = [];
            this.statusWiseIncidents = [];
            this.moduleWiseAssetIncidents = [];
            this.priorityWiseAssetIncidents = [];
            this.statusWiseAssetIncidents = [];
        } else {
            this.moduleWiseIncidents = data.moduleWiseIncidents;
            this.priorityWiseIncidents = data.priorityWiseIncidents;
            this.statusWiseIncidents = data.statusWiseIncidents;
            this.priorityWiseAssetIncidents = data.priorityWiseAssetIncidents;
            this.moduleWiseAssetIncidents = data.moduleWiseAssetIncidents;
            this.statusWiseAssetIncidents = data.statusWiseAssetIncidents;
        }
    }
}
