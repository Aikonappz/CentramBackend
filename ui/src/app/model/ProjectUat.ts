import { Base } from "./Base"
import { Organisation } from "./Organisation"
import { Project } from "./Project"
import { ProjectUatScript } from "./ProjectUatScript"
import { UserVO } from "./UserVO"

export interface ProjectUat extends Base {
    id: number
    project: Project
    moduleId: number
    subModuleId: number
    projectUatScripts: ProjectUatScript[]
    uatComplete: boolean
    organisation: Organisation
    user: UserVO
}