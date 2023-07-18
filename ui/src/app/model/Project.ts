import { Base } from "./Base";
import { Organisation } from "./Organisation";

export class Project extends Base {
    id: number;
    projectType: any;
    name: string;
    label: string;
    code: String;
    watchList: string[];
    inHouse: boolean;
    status: any;
    organisation: Organisation;

    constructor() {
        super();
        this.id = null;
        this.projectType = null;
        this.name = null;
        this.label = null;
        this.code = null;
        this.watchList = [];
        this.status = 'ACTIVE';
        this.inHouse = true;
        this.organisation = new Organisation();
    }
}

export interface ProjectList {
    content: Project[];
    totalElements: number;
}