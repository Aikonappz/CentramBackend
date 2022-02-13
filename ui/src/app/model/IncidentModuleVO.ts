export class IncidentModuleVO {
    moduleId: number;
    module: string;
    moduleName: string;
    count: number;

    constructor() {
        this.moduleId = -1;
        this.module = '';
        this.moduleName = '';
        this.count = -1;
    }
}