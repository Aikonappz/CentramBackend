export class PermissionDTO {
    roleId: number;
    moduleId: number[];
    actionIds: number[];

    constructor() {
        this.roleId = null;
        this.moduleId = [];
        this.actionIds = [];
    }
}