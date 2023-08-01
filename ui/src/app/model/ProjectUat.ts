import { Base } from "./Base"
import { Organisation } from "./Organisation"
import { Project } from "./Project"
import { ProjectUatScript } from "./ProjectUatScript"
import { UserVO } from "./UserVO"

export class ProjectUat extends Base {
    id: number;
    project: Project;
    moduleId: number;
    subModuleId: number;
    projectUatScripts: ProjectUatScript[];    
    organisation: Organisation;
    user: UserVO;
}