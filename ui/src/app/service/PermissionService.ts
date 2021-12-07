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

    public getLoggedInUser(): LoggedInUser {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        return this.loggedInUser;
    }

    public hasPermissionByName(name: string, action: string): boolean {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        for (let j in this.loggedInUser.modulePermissions) {
            if (name.toUpperCase() === this.loggedInUser.modulePermissions[j].moduleName.toUpperCase()
                && this.loggedInUser.modulePermissions[j].actions.includes(action)) {
                return true;
            }
        }
        return false;
    }

    public hasPermissionById(moduleId: number, action: string): boolean {
        this.loggedInUser = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        for (let j in this.loggedInUser.modulePermissions) {
            if (moduleId === this.loggedInUser.modulePermissions[j].moduleId
                && this.loggedInUser.modulePermissions[j].actions.includes(action)) {
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