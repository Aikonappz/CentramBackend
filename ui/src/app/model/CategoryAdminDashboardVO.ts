import { IncidentModuleVO } from "./IncidentModuleVO";
import { IncidentPriorityVO } from "./IncidentPriorityVO";
import { IncidentStatusVO } from "./IncidentStatusVO";

export class CategoryAdminDashboardVO {

    priorityWiseIncidents: IncidentPriorityVO[];
    moduleWiseIncidents: IncidentModuleVO[];
    statusWiseIncidents: IncidentStatusVO[];
    aging5: number;
    aging10: number;
    aging20: number;
    aging30: number;
    aging60: number;
    constructor(data: any) {
        if (data == null) {
            this.moduleWiseIncidents = [];
            this.priorityWiseIncidents = [];
            this.statusWiseIncidents = [];
            this.aging5 = -1;
            this.aging10 = -1;
            this.aging20 = -1;
            this.aging30 = -1;
            this.aging60 = -1;
        } else {
            this.moduleWiseIncidents = data.moduleWiseIncidents;
            this.priorityWiseIncidents = data.priorityWiseIncidents;
            this.statusWiseIncidents = data.statusWiseIncidents;
            this.aging5 = data.aging5;
            this.aging10 = data.aging10;
            this.aging20 = data.aging20;
            this.aging30 = data.aging30;
            this.aging60 = data.aging60;
        }

    }

}
