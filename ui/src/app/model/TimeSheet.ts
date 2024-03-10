import { Base } from "./Base";
import { Project } from "./Project";
import { User } from "./User";


export class TimeSheet extends Base {
    submissionSelection: string;
    id: number;
    startDate: string;
    endDate: string;
    user: User;
    timeSheetEntries: TimeSheetEntry[] = [];
    constructor() {
        super();
    }
}

export interface TimeSheetList {
    content: TimeSheet[];
    totalElements: number;
}

export class TimeSheetEntry extends Base {
    id: number;
    project: Project;
    task: any;
    billingType: any;
    location: any;
    userComment: string;
    timeEntries: Object = {};
    approverComment: string;
    approved: boolean;
    rejected: boolean;
    approver: User;
}