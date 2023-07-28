import { Base } from "./Base"
import { ProjectUatScriptDetail } from "./ProjectUatScriptDetail"

export interface ProjectUatScript extends Base {
    id: number
    testScriptName: string
    testScenario: string
    plannedDate: string
    projectUatScriptDetails: ProjectUatScriptDetail[]
}