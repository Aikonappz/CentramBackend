import { Injectable } from "@angular/core";
import { AppUtility } from "../config/AppUtility";
import { Permission } from "../model/Permssion";

@Injectable({
    providedIn: 'root'
})
export class PermissionService {
    private loggedInUser: any;
    private permissions: Permission[];
    constructor() {

    }

    public hasPermission(module: string, permission: string): boolean {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        this.permissions = this.loggedInUser.modulePermissions;
        for (let j in this.permissions) {
            if (module.toUpperCase() === this.permissions[j].moduleName.toUpperCase()
                && this.permissions[j].actions.includes(permission)) {
                return true;
            }
        }
        return false;
    }

    public hasRole(role: string): boolean {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        this.permissions = this.loggedInUser.modulePermissions;
        return this.loggedInUser.roles.includes(role);
    }

}