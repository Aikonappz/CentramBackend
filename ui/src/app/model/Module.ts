export class Module {
    id: number;
    name: string;
    parentModuleId?: any;
    status: string;
    appModule: boolean;
    licenseType: string;

    constructor() {
        this.id = null;
        this.name = '';
        this.parentModuleId = null;
        this.status = '';
        this.appModule = true;
        this.licenseType = '';
    }
}