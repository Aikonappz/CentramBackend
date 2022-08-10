import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import * as moment from 'moment';
import { tap } from 'rxjs/operators';
import { AppUtility } from '../../config/AppUtility';
import { IncidentStatus } from '../../model/enumerator/IncidentStatus';
import { Incident } from '../../model/Incident';
import { Permission } from '../../model/Permssion';
import { IncidentDataSource } from '../../service/datasource/IncidentDataSource';
import { IncidentService } from '../../service/IncidentService';
import { LoggedInUserService } from '../../service/LoggedInUserService';
declare var $: any;

@Component({
  selector: 'app-ncident',
})
export class IncidentComponent implements OnInit {
  constructor(
    private fb: FormBuilder,
    private titleService: Title,
    private router: Router,
    private service: IncidentService,
    private loggedInUserService: LoggedInUserService,
    private route: ActivatedRoute,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        titleService.setTitle(title);
      }
    });
    let loggedInUser = this.loggedInUserService.getLoggedInUser();
    //console.log("Sasasjakh kj");
    this.route.params.subscribe(params => {
      let targetroute = this.route.snapshot.paramMap.get('targetroute');
      if (targetroute == 'user-route') {
        this.router.navigate(['incident/user/all']);
      } else if (targetroute == 'agent-route') {
        this.router.navigate(['incident/agent/all']);
      } else {
        // ORG_INCIDENT_AGENT_LEAD
        // ORG_INCIDENT_AGENT_MANAGER
        // ORG_USER_INCIDENT
        // ORG_AGENT_INCIDENT
        if (loggedInUser.roles.includes("ORG_USER_INCIDENT")) {
          this.router.navigate(['incident/user/all']);
        } else if (loggedInUser.roles.includes("ORG_AGENT_INCIDENT")) {
          this.router.navigate(['incident/agent/all']);
        }
      }
    });
    //console.log(loggedInUser.roles);
  }

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

  ngOnInit(): void {
  }

  ngAfterViewInit() {
  }
}