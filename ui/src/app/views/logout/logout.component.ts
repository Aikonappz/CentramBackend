import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { AppUtility } from '../../config/AppUtility';
import { UserService } from '../../service/UserService';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(titleService: Title, private router: Router, private userService: UserService,) {
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

  callSignOutService(){
    this.userService
      .signOutService()
      .subscribe((data: any) => {
        localStorage.removeItem(AppUtility.LOGED_IN_PROFILE_JWT);
        localStorage.removeItem(AppUtility.LOGED_IN_PROFILE);
        //console.log(data);
        //this.angForm.reset();
        //this.toggleStockAddMode();
        //window.location.reload();
        this.router.navigate(['/']);
      });
  }

}
