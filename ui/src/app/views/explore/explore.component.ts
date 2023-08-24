import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { DashboardService } from '../../service/DashboardService';
import { MiscService } from '../../service/MiscService';
import { Module } from '../../model/Module';
import { environment } from '../../../environments/environment';
import { ClientStorageService } from '../../service/ClientStorageService';
import { AppUtility } from '../../config/AppUtility';
declare var $: any;

@Component({
  templateUrl: 'explore.component.html',
  styleUrls: ['explore.component.scss']
})
export class ExploreComponent implements OnInit {
  roles: string[];
  loggedInUser: LoggedInUser;
  modules: Module[];
  modulesChunkedData: any[] = [];
  appUrl: string;

  /**
   * 
   * @param loggedInUserService 
   * @param titleService 
   * @param router 
   * @param service 
   * @param miscService 
   * @param modalService 
   */
  constructor(
    private loggedInUserService: LoggedInUserService,
    private titleService: Title,
    private router: Router,
    private clientStorageService: ClientStorageService,
    private miscService: MiscService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    //console.log(this.loggedInUser);
  }

  /**
   * 
   * @param state 
   * @param parent 
   * @returns 
   */
  getTitle(state, parent) {
    var data = [];
    if (parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }
    if (state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  /**
   * 
   */
  ngOnInit(): void {
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.roles = this.loggedInUser.roles;
    this.loggedInUser.orgAdmin = this.loggedInUserService.hasRole("ORG_ADMIN");
    this.loggedInUser.appManager = this.loggedInUserService.hasRole("APP_ADMIN");
    this.appUrl = environment.appUrl;
    if (!this.loggedInUser.appManager) {
      this.miscService
        .appModulesService({})
        .subscribe((data: any) => {
          this.modules = []; //data.content;
          console.log(JSON.stringify(data.content));
          for (let k = 0; k < data.content.length; k++) {
            if (data.content[k].appModule && data.content[k].parentModuleId === null && data.content[k].appFeatureModule === true) {
              this.modules.push(data.content[k]);
            }
          }
          this.modulesChunkedData = this.chunk(this.modules, 6);
          //console.log(this.modulesChunkedData);
        });
    }
  }

  /**
   * 
   * @param arr 
   * @param size 
   * @returns 
   */
  chunk(arr: any[], size: number): any[] {
    var newArr = [];
    for (var i = 0; i < arr.length; i += size) {
      newArr.push(arr.slice(i, i + size));
    }
    return newArr;
  }

  /**
   * 
   * @param name 
   * @returns 
   */
  checkPermission(name: string): boolean {
    return this.loggedInUser.licenseType === 'ALL' || this.loggedInUser.licenseType === name.toLocaleUpperCase();
  }

  /**
   * 
   * @param modulePath 
   */
  redirectToModule(modulePath: string) {
    let paths: string[] = modulePath.split("/");
    //this.clientStorageService.set(AppUtility.LAST_EXPLORED_MODULE_KEY, paths[0].toLocaleUpperCase());
    sessionStorage.setItem(AppUtility.LAST_EXPLORED_MODULE_KEY, paths[0].toLocaleUpperCase());
    this.router.navigate([]).then(result => { window.open('/' + modulePath, '_blank'); });
  }

}