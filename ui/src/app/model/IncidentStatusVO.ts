export class IncidentStatusVO {
    moduleId: number;
    status: string;
    statusName: string;
    count: number;

    constructor() {
        this.moduleId = -1;
        this.status = '';
        this.statusName = '';
        this.count = -1;
    }
}