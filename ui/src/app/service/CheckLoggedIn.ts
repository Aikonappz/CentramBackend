import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from "@angular/router";
import { AppUtility } from "../config/AppUtility";
import { LoggedInUser } from "../model/LoggedInUser";
import { ClientStorageService } from "./ClientStorageService";
import { LoggedInUserService } from "./LoggedInUserService";

@Injectable({
    providedIn: 'root' // just before your class
})
export class CheckLoggedIn implements Resolve<any> {
    private loggedInUser: LoggedInUser;
    constructor(
        private router: Router,
        private loggedInUserService: LoggedInUserService,
        private clientStorageService: ClientStorageService,
    ) { }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        this.loggedInUser = this.loggedInUserService.getLoggedInUser();
        if (this.loggedInUser != null && this.loggedInUser.jwtToken != null && this.loggedInUser.jwtToken.replace(/\s/g, "") != "") {
            return true;
        } else {
            this.clientStorageService.set(AppUtility.LOGED_IN_LAST_VISIT, this.router.url);
            this.router.navigate(['/sign-in']);
            return false;
        }
    }
}