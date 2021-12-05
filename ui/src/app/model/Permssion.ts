
export class Permission {
    public moduleId: number;
    public moduleName: string;
    public moduleParentId: null;
    public licenseType: string;
    public appModule: boolean;
    public actionNames: string;
    public actions: string[];
    customerModuleName: string;

    private constructor(d: any) {
        this.moduleId = d.moduleId;
        this.moduleName = d.moduleName;
        this.moduleParentId = d.moduleParentId;
        this.licenseType = d.licenseType;
        this.appModule = d.appModule;
        this.actionNames = d.actionNames;
        this.actions = [];
        this.customerModuleName = '';
    }
}