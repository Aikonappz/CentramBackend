import { IncidentModuleVO } from "./IncidentModuleVO";
import { IncidentStatusVO } from "./IncidentStatusVO";

export class UserDashboardVO {
    moduleWiseIncidents: IncidentModuleVO[];
    statusWiseIncidents: IncidentStatusVO[];
    moduleWiseAssetIncidents: IncidentModuleVO[];
    statusWiseAssetIncidents: IncidentStatusVO[];
    constructor(data: any) {
        if (data == null) {
            this.moduleWiseIncidents = [];
            this.statusWiseIncidents = [];
            this.moduleWiseAssetIncidents = [];
            this.statusWiseAssetIncidents = [];
        } else {
            this.moduleWiseIncidents = data.moduleWiseIncidents;
            this.statusWiseIncidents = data.statusWiseIncidents;
            this.moduleWiseAssetIncidents = data.moduleWiseAssetIncidents;
            this.statusWiseAssetIncidents = data.statusWiseAssetIncidents;
        }
    }
}
