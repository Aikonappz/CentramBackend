import { Base } from "./Base"
import { Organisation } from "./Organisation"
import { Project } from "./Project"
import { ProjectUatScript } from "./ProjectUatScript"
import { UserVO } from "./UserVO"

export class ProjectUat extends Base {
    id: number;
    project: Project;
    moduleId: number;
    uatCycleName: string;
    label: string;
    subModuleId: number;
    projectUatScripts: ProjectUatScript[];    
    organisation: Organisation;
    user: UserVO;
    uatCycleComplete: boolean;
}

export interface ProjectUatList {
    content: ProjectUat[];
    totalElements: number;
}