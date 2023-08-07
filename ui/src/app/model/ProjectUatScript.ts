import { Base } from "./Base"
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
}