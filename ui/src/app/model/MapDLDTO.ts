import { Base } from "./Base";
import { Organisation } from "./Organisation";

export class MapDLDTO extends Base {
    id: number;
    moduleId: number;
    subModuleId: number[];
    dlName: string;
    organisation: Organisation;

    constructor() {
        super();
        this.id = null;
        this.moduleId = null;
        this.subModuleId = null;
        this.dlName = '';
        this.organisation = new Organisation();
    }
}