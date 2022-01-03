import { Base } from "./Base";
import { Organisation } from "./Organisation";

export class MapDLVO extends Base {
    id: number;
    moduleId: number;
    moduleName: string;
    subModuleId: number;
    subModuleName: string;
    dlName: string;
    organisationId: number;

    constructor() {
        super();
        this.id = null;
        this.moduleId = null;
        this.subModuleId = null;
        this.dlName = '';
        this.moduleName = '';
        this.subModuleName = '';
        this.organisationId = null;
    }
}


export interface MapDLVOList {
    content: MapDLVO[];
    totalElements: number;
}