export class PermissionDTO {
    roleId: number;
    moduleIds: number[];
    actionIds: number[];

    constructor() {
        this.roleId = null;
        this.moduleIds = [];
        this.actionIds = [];
    }
}