import { Base } from "./Base";
import { Organisation } from "./Organisation";

export class Project extends Base {
    id: number;
    projectType: any;
    projectBillingType: any;
    name: string;
    label: string;
    code: String;
    watchList: string[];
    stakeHolders: string[];
    consultants: string[];
    inHouse: boolean;
    status: any;
    organisation: Organisation;
    start: any;
    end: any;
    technology: any;
    moduleId: number;
    moduleName: string;
    subModuleId: number;
    subModuleName: string;
    projectFor: any;

    constructor() {
        super();
        this.id = null;
        this.projectType = null;
        this.projectBillingType = null;
        this.name = null;
        this.label = null;
        this.code = null;
        this.watchList = [];
        this.stakeHolders = [];
        this.consultants = [];
        this.status = 'ACTIVE';
        this.inHouse = true;
        this.technology = null;
        this.moduleId = null;
        this.subModuleId = null;
        this.start = null;
        this.end = null;
        this.organisation = new Organisation();
    }
}

export interface ProjectList {
    content: Project[];
    totalElements: number;
}