
export class Permission {
    public moduleId: number;
    public moduleName: string;
    public moduleParentId: null;
    public licenseType: string;
    public appModule: boolean;
    public actions: string[];
    public customerModuleName: string;
    public roleId: number;
    public roleName: string;
    public actionId: number;
    public actionName: string;

    public constructor(d: any) {
        this.moduleId = d.moduleId;
        this.moduleName = d.moduleName;
        this.moduleParentId = d.moduleParentId;
        this.licenseType = d.licenseType;
        this.appModule = d.appModule;
        this.actions = d.actionName.split(',');
        this.customerModuleName = d.customerModuleName;
        this.actionName = d.actionName;
        this.actionId = d.actionId;
        this.roleId = d.roleId;
        this.roleName = d.roleName;
    }
}