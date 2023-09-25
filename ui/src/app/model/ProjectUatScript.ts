import { Base } from "./Base"
import { MediaFile } from "./MediaFile";
import { ProjectUatScriptDetail } from "./ProjectUatScriptDetail"

export class ProjectUatScript extends Base {
    id: number;
    testCaseId: string;
    testCaseDescription: string;
    testScriptName: string;
    testScenario: string;
    plannedDate: string;
    uatComplete: boolean;
    projectUatScriptDetails: ProjectUatScriptDetail[];
    label: string;
    status: string;
    uatManual: MediaFile;
    uatScript: MediaFile;
    canMarkComplete: Boolean;
}

export interface ProjectUatScriptList {
    content: ProjectUatScript[];
    totalElements: number;
}