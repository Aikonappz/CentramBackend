export class IncidentPriorityVO {
    priorityId: number;
    priority: string;
    count: number;

    constructor() {
        this.priorityId = -1;
        this.priority = '';
        this.count = -1;
    }
}