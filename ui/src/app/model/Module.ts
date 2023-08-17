export class Module {
    id: number;
    name: string;
    parentModuleId?: any;
    status: any;
    appModule: boolean;
    appFeatureModule: boolean;
    appModuleDescription: string;
    licenseType: string;
    customerModuleName: string;
    appModulePath: string;
    appModuleIcon: string;
    
    constructor() {
        this.id = null;
        this.name = '';
        this.parentModuleId = null;
        this.status = '';
        this.appModule = true;
        this.appFeatureModule = false;
        this.appModuleDescription = '';
        this.licenseType = '';
        this.customerModuleName = '';
    }
}