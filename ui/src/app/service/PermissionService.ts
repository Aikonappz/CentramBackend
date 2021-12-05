import { Injectable } from "@angular/core";
import { AppUtility } from "../config/AppUtility";
import { LoggedInUser } from "../model/LoggedInUser";
import { Permission } from "../model/Permssion";

@Injectable({
    providedIn: 'root'
})
export class LoggedInUserService {
    private loggedInUser: LoggedInUser;
    constructor() { }

    public hasPermission(module: string, permission: string): boolean {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        for (let j in this.loggedInUser.modulePermissions) {
            if (module.toUpperCase() === this.loggedInUser.modulePermissions[j].moduleName.toUpperCase()
                && this.loggedInUser.modulePermissions[j].actions.includes(permission)) {
                return true;
            }
        }
        return false;
    }

    public hasRole(role: string): boolean {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        return this.loggedInUser.roles.includes(role);
    }

    public getRoles(): string[] {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        return this.loggedInUser.roles;
    }

    public getModulePermissions(): Permission[] {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        return this.loggedInUser.modulePermissions;
    }

}