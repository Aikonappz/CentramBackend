import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { AppUtility } from '../../config/AppUtility';
import { ClientStorageService } from '../../service/ClientStorageService';
import { NotificationWSService } from '../../service/NotificationWSService';
import { UserService } from '../../service/UserService';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(
    private titleService: Title,
    private router: Router,
    private userService: UserService,
    private websocketService: NotificationWSService,
    private clientStorageService: ClientStorageService,
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        //console.log('title', title);
        titleService.setTitle(title);
      }
    });
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
    this.callSignOutService();
  }

  callSignOutService() {
    this.userService
      .signOutService()
      .subscribe((data: any) => {
        //clearInterval();
        this.clientStorageService.remove(AppUtility.LOGGED_IN_PROFILE);
        this.clientStorageService.remove(AppUtility.APP_LOGOUT_WARNING_MODAL_STATUS_KEY);
        this.clientStorageService.remove(AppUtility.APP_LAST_ACTION_KEY);
        this.clientStorageService.clear();
        //console.log(data);
        //this.angForm.reset();
        //this.toggleStockAddMode();
        //window.location.reload();
        this.websocketService.disconnect();
        this.router.navigate(['/'])
          .then(() => {
            window.location.reload();
          });
      });
  }

}
