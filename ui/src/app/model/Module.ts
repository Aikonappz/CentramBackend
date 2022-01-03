export class Module {
    id: number;
    name: string;
    parentModuleId?: any;
    status: any;
    appModule: boolean;
    licenseType: string;
    customerModuleName: string;

    constructor() {
        this.id = null;
        this.name = '';
        this.parentModuleId = null;
        this.status = '';
        this.appModule = true;
        this.licenseType = '';
        this.customerModuleName = '';
    }
}