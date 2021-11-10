import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { AppSettings } from "../config/AppSettings";
import { navItems } from "../_nav";

@Injectable({
    providedIn: 'root' // just before your class
})
export class MenuService {
    href: string;
    constructor(private router: Router) { }
    buildMenu() {
        navItems.forEach(function (value) {
            value.name = value.name + '1';
            //console.log(value);
        });
    }
}