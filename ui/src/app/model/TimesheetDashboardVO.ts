export class TimesheetDashboardVO {
    approvedCount: number;
    pendingCount: number;

    constructor() {
        this.approvedCount = -1;
        this.pendingCount = -1;
    }
}
