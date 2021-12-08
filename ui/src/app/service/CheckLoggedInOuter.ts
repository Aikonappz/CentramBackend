import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from "@angular/router";
import { LoggedInUser } from "../model/LoggedInUser";
import { LoggedInUserService } from "./LoggedInUserService";

@Injectable({
    providedIn: 'root' // just before your class
})
export class CheckLoggedInOuter implements Resolve<any> {
    private loggedInUser: LoggedInUser;

    constructor(
        private router: Router,
        private loggedInUserService: LoggedInUserService,
    ) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        this.loggedInUser = this.loggedInUserService.getLoggedInUser();
        if (this.loggedInUser != null && this.loggedInUser.jwtToken != null && this.loggedInUser.jwtToken.replace(/\s/g, "") != "") {
            //window.alert("aleady logged in");
            //let lastVisitedPage = localStorage.getItem(AppSettings.LOGED_IN_LAST_VISIT);
            // if (lastVisitedPage != null) {
            //     console.log(lastVisitedPage);
            //     //this.router.navigate([lastVisitedPage]);
            // } else {
            //     this.router.navigate(['/dashboard']);
            // }
            this.router.navigate(['/dashboard']);
            return true;
        } else {
            //window.alert("not logged in");
            return false;
        }
    }
}