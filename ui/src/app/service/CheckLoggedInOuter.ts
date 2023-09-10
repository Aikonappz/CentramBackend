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
export class CheckLoggedInOuter implements Resolve<any> {
    private loggedInUser: LoggedInUser;

    constructor(
        private router: Router,
        private loggedInUserService: LoggedInUserService,
        private clientStorageService: ClientStorageService,
        private location: Location,
        private locationStrategy: LocationStrategy,
    ) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        this.loggedInUser = this.loggedInUserService.getLoggedInUser();
        if (this.loggedInUser != null && this.loggedInUser.jwtToken != null && this.loggedInUser.jwtToken.replace(/\s/g, "") != "") {
            /*let lastVisitedPage = this.clientStorageService.get(AppUtility.LOGGED_IN_LAST_VISIT);
            if (lastVisitedPage != null) {
                console.log(lastVisitedPage);
                //this.router.navigate([lastVisitedPage]);
            } else {
                this.router.navigate(['/explore']);
            }*/
            this.router.navigate(['/' + AppUtility.EXPLORE_LANDING_PAGE_PATH]);
            return true;
        } else {
            //window.alert("not logged in");
            return false;
        }
    }
}