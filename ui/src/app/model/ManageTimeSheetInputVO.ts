import { Project } from "./Project";
import { TimeSheetTimeEntry } from "./TimeSheetTimeEntry";

export class ManageTimeSheetInputVO {
    projects: Project[];
    tasks: any[];
    locations: any[];
    billingTypes: any[];
    timeEntries: TimeSheetTimeEntry[];

    constructor() {
    }
}