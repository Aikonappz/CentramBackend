export class IncidentStatusVO {
    status: string;
    statusName: string;
    count: number;

    constructor() {
        this.status = '';
        this.statusName = '';
        this.count = -1;
    }
}