import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

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
