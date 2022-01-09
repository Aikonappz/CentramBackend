export class OrgAdminDashboardVO {
    activeEmployees: number;
    inHouseAgents: number;
    outSourcedAgents: number;
    constructor() {
        this.activeEmployees = -1;
        this.inHouseAgents = -1;
        this.outSourcedAgents = -1;
    }
}
