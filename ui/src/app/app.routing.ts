import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Import Containers
import { DefaultLayoutComponent } from './containers';

import { P404Component } from './views/error/404.component';
import { P500Component } from './views/error/500.component';
import { LoginComponent } from './views/login/login.component';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './views/reset-password/reset-password.component';
import { LogoutComponent } from './views/logout/logout.component';
import { CheckLoggedIn } from './service/CheckLoggedIn';
import { CheckLoggedInOuter } from './service/CheckLoggedInOuter';
import { environment } from '../environments/environment';
import { LandingComponent } from './views/landing/landing.component';
import { SSOLoginComponent } from './views/sso-login/sso-login.component';
import { AppUtility } from './config/AppUtility';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    data: {
      title: environment.appName
    },
    pathMatch: 'full',
  },
  {
    path: 'sign-in',
    component: LoginComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: environment.appName + ' - ' + 'Sign In'
    }
  },
  {
    path: 'sign-in/:mode',
    component: LoginComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: environment.appName + ' - ' + 'Sign In'
    }
  },
  {
    path: 'sso/sign-in/:principal',
    component: SSOLoginComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: environment.appName + ' - ' + 'SSO Sign In'
    }
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: environment.appName + ' - ' + 'Forgot Password'
    }
  },
  {
    path: 'reset-password/:id',
    component: ResetPasswordComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: environment.appName + ' - ' + 'Reset Password'
    }
  },
  // {
  //   path: 'request-demo',
  //   component: RequestDemoComponent,
  //   resolve: { myData: CheckLoggedInOuter },
  //   data: {
  //     title: environment.appName + ' - ' + 'Request a Demo'
  //   }
  // },
  {
    path: 'sign-out',
    component: LogoutComponent,
    resolve: { myData: CheckLoggedIn },
    data: {
      title: environment.appName + ' - ' + 'Sign Out'
    }
  },
  {
    path: '404',
    component: P404Component,
    data: {
      title: environment.appName + ' - ' + 'Page 404'
    }
  },
  {
    path: '500',
    component: P500Component,
    data: {
      title: environment.appName + ' - ' + 'Page 500'
    }
  },
  {
    path: '',
    component: DefaultLayoutComponent,
    data: {
      title: 'Home'
    },
    children: [
      {
        path: AppUtility.EXPLORE_LANDING_PAGE_PATH,
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/explore/explore.module').then(m => m.ExploreModule)
      },
      {
        path: 'supadmin',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/supadmin/supadmin.module').then(m => m.SuperAdminModule),
      },
      {
        path: 'admin',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/admin/admin.module').then(m => m.AdminModule),
      },
      {
        path: 'dashboard',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/dashboard/dashboard.module').then(m => m.DashboardModule)
      },
      // {
      //   path: 'user',
      //   resolve: { myData: CheckLoggedIn },
      //   loadChildren: () => import('./views/user/user.module').then(m => m.UserModule),
      // },
      
      {
        path: 'notification',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/notification/notification.module').then(m => m.NotificationModule),
      },
      {
        path: 'master',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/master/master.module').then(m => m.MasterModule),
      },
      {
        path: 'incident',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/incident/incident.module').then(m => m.IncidentModule),
      },
      {
        path: 'asset',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/asset/asset.module').then(m => m.AssetModule),
      },
      {
        path: 'project',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/project/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'uat',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/uat/uat.module').then(m => m.UATModule),
      },
      {
        path: 'timesheet',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/timesheet/time-sheet.module').then(m => m.TimesheetModule),
      },
      {
        path: 'report',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/report/report.module').then(m => m.ReportModule),
      },
    ]
  },
  { path: '**', component: P404Component }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
