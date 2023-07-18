import { Base } from "./Base";
import { Project } from "./Project";
import { User } from "./User";


export class TimeSheet extends Base {
    startDate: string;
    endDate: string;
    project: Project;
    user: User;
    referenceId: string;
    task: string;
    location: string;
    billingType: string;
    timeSheetEntries: any;
    approver: User;
    approverTookAction: Boolean;
    approverComment: String;
    approved: Boolean;
    submissionSelection: string = "DAILY";
    constructor() {
        super();

    }
}

export interface TimeSheetList {
    content: TimeSheet[];
    totalElements: number;
}