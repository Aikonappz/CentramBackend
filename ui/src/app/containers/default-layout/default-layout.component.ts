import {Component} from '@angular/core';
import * as moment from 'moment';
import { AppSettings } from '../../config/AppSettings';
import { navItems } from '../../_nav';

@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html'
})
export class DefaultLayoutComponent {

  public sidebarMinimized = false;
  public navItems = navItems;  
  public appUrl;
  public appBrandName;
  public appDevName;
  public currentYear;
  
  constructor(){
    let m = moment();  
    this.appUrl = AppSettings.APP_URL;
    this.appBrandName = AppSettings.APP_BRAND_NAME;
    this.appDevName = AppSettings.APP_DEV_NAME;
    this.currentYear = m.format('YYYY');
    
    navItems.forEach(function(value){

    });

  }
  
  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }
}
