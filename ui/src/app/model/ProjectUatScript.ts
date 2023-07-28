import { Base } from "./Base"
import { ProjectUatScriptDetail } from "./ProjectUatScriptDetail"

export class ProjectUatScript extends Base {
    id: number;
    testScriptName: string;
    testScenario: string;
    plannedDate: string;
    projectUatScriptDetails: ProjectUatScriptDetail[];
    label: string;
}