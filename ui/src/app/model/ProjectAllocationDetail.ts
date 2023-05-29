import { Base } from "./Base";
export class ProjectAllocationDetail extends Base {
    id: number;
    project: Object;
    user: Object;
    maxAllocation: String;
    startDate: String;
    endDate: String;
    deallocated: Boolean;
    constructor() {
        super();
    }
}
export interface ProjectAllocationDetailList {
    content: ProjectAllocationDetail[];
    totalElements: number;
}