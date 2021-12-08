import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from "@angular/router";
import { AppUtility } from "../config/AppUtility";
import { LoggedInUser } from "../model/LoggedInUser";
import { LocalStorageService } from "./LocalStorageService";
import { LoggedInUserService } from "./LoggedInUserService";

@Injectable({
    providedIn: 'root' // just before your class
})
export class CheckLoggedIn implements Resolve<any> {
    private loggedInUser: LoggedInUser;
    constructor(
        private router: Router,
        private loggedInUserService: LoggedInUserService,
    ) { }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        this.loggedInUser = this.loggedInUserService.getLoggedInUser();
        if (this.loggedInUser != null && this.loggedInUser.jwtToken != null && this.loggedInUser.jwtToken.replace(/\s/g, "") != "") {
            return true;
        } else {
            LocalStorageService.set(AppUtility.LOGED_IN_LAST_VISIT, btoa(this.router.url));
            this.router.navigate(['/sign-in']);
            return false;
        }
    }
}