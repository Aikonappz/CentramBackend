import { Injectable } from "@angular/core";
import { AppUtility } from "../config/AppUtility";
import { LoggedInUser } from "../model/LoggedInUser";
import { Permission } from "../model/Permssion";
import { ClientStorageService } from "./ClientStorageService";

@Injectable({
    providedIn: 'root'
})
export class LoggedInUserService {
    private loggedInUser: LoggedInUser;
    constructor(
        private clientStorageService: ClientStorageService,
    ) { }
    public appManager(): boolean {
        this.loggedInUser = this.getLoggedInUser();
        return this.loggedInUser.appManager;
    }
    public getLoggedInUser(): LoggedInUser {
        if (this.clientStorageService.get(AppUtility.LOGGED_IN_PROFILE)) {
            this.loggedInUser = JSON.parse(this.clientStorageService.get(AppUtility.LOGGED_IN_PROFILE));
            return this.loggedInUser;
        }
    }
    public hasPermissionByName(name: string, action: string): boolean {
        this.loggedInUser = this.getLoggedInUser();
        for (let j in this.loggedInUser.modulePermissions) {
            if (name.toUpperCase() === this.loggedInUser.modulePermissions[j].moduleName.toUpperCase()
                && this.loggedInUser.modulePermissions[j].actions.includes(action)) {
                return true;
            }
        }
        return false;
    }
    public hasPermissionById(moduleId: number, action: string): boolean {
        this.loggedInUser = this.getLoggedInUser();
        for (let j in this.loggedInUser.modulePermissions) {
            //console.log(JSON.stringify(this.loggedInUser.modulePermissions[j]));
            if (moduleId === this.loggedInUser.modulePermissions[j].moduleId
                && this.loggedInUser.modulePermissions[j].actions.includes(action)) {
                return true;
            }
        }
        return false;
    }
    public hasRole(role: string): boolean {
        this.loggedInUser = this.getLoggedInUser();
        return this.loggedInUser.roles.includes(role);
    }
    public getRoles(): string[] {
        this.loggedInUser = this.getLoggedInUser();
        return this.loggedInUser.roles;
    }
    public getModulePermissions(): Permission[] {
        this.loggedInUser = this.getLoggedInUser();
        return this.loggedInUser.modulePermissions;
    }
}