import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from "@angular/router";
import { AppSettings } from "../config/AppSettings";

@Injectable({
    providedIn: 'root' // just before your class
})
export class CheckLoggedIn implements Resolve<any> {
    constructor(private router: Router) { }
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (localStorage.getItem(AppSettings.LOGED_IN_PROFILE_JWT) && localStorage.getItem(AppSettings.LOGED_IN_PROFILE)) {
            return true;
        } else {
            localStorage.setItem(AppSettings.LOGED_IN_LAST_VISIT, this.router.url);
            this.router.navigate(['/sign-in']);
            return false;
        }
    }
}