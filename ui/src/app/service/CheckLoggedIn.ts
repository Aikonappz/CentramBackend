import { LocationStrategy, Location } from "@angular/common";
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
        private location: Location,
        private locationStrategy: LocationStrategy
    ) { }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        this.loggedInUser = this.loggedInUserService.getLoggedInUser();
        if (this.loggedInUser != null && this.loggedInUser.jwtToken != null && this.loggedInUser.jwtToken.replace(/\s/g, "") != "") {
            return true;
        } else {
            /*console.log(this.location.path());
            console.log(this.location.prepareExternalUrl('/'));
            console.log(this.locationStrategy.getBaseHref());*/
            this.clientStorageService.set(AppUtility.LOGGED_IN_LAST_VISIT, this.location.path());
            this.router.navigate(['/sign-in']);
            return false;
        }
    }
}