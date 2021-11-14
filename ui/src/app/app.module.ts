import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { LocationStrategy, HashLocationStrategy, DatePipe } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { PERFECT_SCROLLBAR_CONFIG } from 'ngx-perfect-scrollbar';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';

import { IconModule, IconSetModule, IconSetService } from '@coreui/icons-angular';

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

import { AppComponent } from './app.component';

// Import containers
import { DefaultLayoutComponent } from './containers';

import { P404Component } from './views/error/404.component';
import { P500Component } from './views/error/500.component';
import { LoginComponent } from './views/login/login.component';
import { RegisterComponent } from './views/register/register.component';

const APP_CONTAINERS = [
  DefaultLayoutComponent
];

import {
  AppAsideModule,
  AppBreadcrumbModule,
  AppHeaderModule,
  AppFooterModule,
  AppSidebarModule,
} from '@coreui/angular';

// Import routing module
import { AppRoutingModule } from './app.routing';

// Import 3rd party components
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ChartsModule } from 'ng2-charts';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './views/reset-password/reset-password.component';
import { RequestDemoComponent } from './views/request-demo/request-demo.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthHtppInterceptorService } from './service/AuthHtppInterceptorService';
import { MiscService } from './service/MiscService';
import { ApiHttpService } from './service/ApiHttpService';
import { UserService } from './service/UserService';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';

import { LogoutComponent } from './views/logout/logout.component';

import { CheckLoggedIn } from './service/CheckLoggedIn';
import { CheckLoggedInOuter } from './service/CheckLoggedInOuter';
import { UserModule } from './views/user/user.module';
import { OrganisationComponent } from './views/organisation/organisation.component';

@NgModule({
  imports: [
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    AppAsideModule,
    AppBreadcrumbModule.forRoot(),
    AppFooterModule,
    AppHeaderModule,
    AppSidebarModule,
    PerfectScrollbarModule,
    BsDropdownModule.forRoot(),
    TabsModule.forRoot(),
    ChartsModule,
    MatTableModule,
    MatPaginatorModule,
    IconModule,
    IconSetModule.forRoot(),
    UserModule
  ],
  declarations: [
    AppComponent,
    ...APP_CONTAINERS,
    P404Component,
    P500Component,
    RegisterComponent,

    LoginComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    RequestDemoComponent,
    LogoutComponent,
  ],
  providers: [
    CheckLoggedInOuter,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthHtppInterceptorService,
      multi: true,
    },
    ApiHttpService,
    MiscService,
    UserService,
    CheckLoggedIn,
    {
      provide: LocationStrategy,
      useClass: HashLocationStrategy
    },
    DatePipe,
    IconSetService,

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
