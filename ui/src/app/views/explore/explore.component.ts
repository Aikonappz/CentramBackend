import { Component, OnInit, } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import { LoggedInUserService } from '../../service/LoggedInUserService';
import { LoggedInUser } from '../../model/LoggedInUser';
import { DashboardService } from '../../service/DashboardService';
import { AdminDashboardVO } from '../../model/AdminDashboardVO';
import { BsModalRef, BsModalService, ModalOptions } from 'ngx-bootstrap/modal';
import * as moment from 'moment';
import { Label, MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';
import { OrgAdminDashboardVO } from '../../model/OrgAdminDashboardVO';
import { UserDashboardVO } from '../../model/UserDashboardVO';
import { AgentDashboardVO } from '../../model/AgentDashboardVO';
import { CategoryAdminDashboardVO } from '../../model/CategoryAdminDashboardVO';
import { ViewIncidentDetails } from './modal/ViewIncidentDetails';
import { ViewAppAdminDashboardDetails } from './modal/ViewAppAdminDashboardDetails';
import { ViewOrgAdminDashboardUserDetails } from './modal/ViewOrgAdminDashboardUserDetails';
import { ViewOrgAdminDashboardVendorDetails } from './modal/ViewOrgAdminDashboardVendorDetails';
import { ViewUserDashboardDetails } from './modal/ViewUserDashboardDetails';
import { MiscService } from '../../service/MiscService';
import { Module } from '../../model/Module';
import { environment } from '../../../environments/environment';
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
    private service: DashboardService,
    private miscService: MiscService,
    private modalService: BsModalService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    this.loggedInUser = this.loggedInUserService.getLoggedInUser();
    this.roles = this.loggedInUser.roles;
    this.appUrl = environment.appUrl;
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
    this.miscService
      .appModulesService({})
      .subscribe((data: any) => {
        this.modules = []; //data.content;
        for (let k = 0; k < data.content.length; k++) {
          if (data.content[k].appModule && data.content[k].parentModuleId === null && data.content[k].appFeatureModule === true) {
            this.modules.push(data.content[k]);
          }
        }
        this.modulesChunkedData = this.chunk(this.modules, 6);
      });
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
    this.router.navigate([]).then(result => { window.open('/' + modulePath, '_blank'); });
  }

}