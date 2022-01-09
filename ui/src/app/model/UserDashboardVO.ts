export class UserDashboardVO {
    openIncidents: number;
    assignedIncidents: number;
    closedIncidents: number;
    constructor() {
        this.openIncidents = -1;
        this.assignedIncidents = -1;
        this.closedIncidents = -1;
    }
}
