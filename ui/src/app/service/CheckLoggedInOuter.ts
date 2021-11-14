import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from "@angular/router";
import { AppUtility } from "../config/AppUtility";

@Injectable({
    providedIn: 'root' // just before your class
})
export class CheckLoggedInOuter implements Resolve<any> {

    constructor(private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (localStorage.getItem(AppUtility.LOGED_IN_PROFILE_JWT) && localStorage.getItem(AppUtility.LOGED_IN_PROFILE)) {
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