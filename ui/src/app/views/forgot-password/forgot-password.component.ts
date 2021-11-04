import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  constructor(titleService: Title, router: Router) {
    router.events.subscribe(event => {
      if(event instanceof NavigationEnd) {
        var title = this.getTitle(router.routerState, router.routerState.root).join('-');
        console.log('title', title);
        titleService.setTitle(title);
      }
    });
  }
  getTitle(state, parent) {
    var data = [];
    if(parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }
    if(state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  ngOnInit(): void {
  }

}
