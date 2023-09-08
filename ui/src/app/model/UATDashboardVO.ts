export class UATDashboardVO {
    inProgress: number;
    completed: number;
    notStarted: number;
    total: number;

    constructor() {
        this.inProgress = -1;
        this.completed = -1;
        this.notStarted = -1;
        this.total = -1;
    }
}
