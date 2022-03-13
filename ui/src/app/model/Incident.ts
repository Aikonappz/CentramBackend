import { Base } from "./Base";
import { IncidentStatus } from "./enumerator/IncidentStatus";
import { Status } from "./enumerator/Status";
import { IncidentCommunication } from "./IncidentCommunication";
import { Priority } from "./Priority";
import { User } from "./User";

export class Incident extends Base {
    id: number;
    moduleId: number;
    subModuleId: number;
    title: string;
    incidentNo: string;
    priority: Priority;
    watchList: string[];
    moduleName: string;
    subModuleName: string;
    prevStatus: any;
    status: any;
    raisedUser: User;
    assignedUser: User;
    communications: IncidentCommunication[];
    raisedAt: any;
    slaAt: any;
    holdAt: any;
    organisation: any;
    incidentType: any;
    constructor() {
        super();
        this.id = null;
        this.moduleId = null;
        this.subModuleId = null;
        this.title = '';
        this.incidentNo = '';
        this.priority = new Priority();
        this.watchList = [];
        this.status = IncidentStatus.OPEN;
        this.raisedUser = new User();
        this.assignedUser = new User();
        this.communications = [];
        this.raisedAt = null;
        this.slaAt = null;
        this.incidentType = 1;
    }
}
export interface IncidentList {
    content: Incident[];
    totalElements: number;
}