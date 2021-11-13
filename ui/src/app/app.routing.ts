import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Import Containers
import { DefaultLayoutComponent } from './containers';

import { P404Component } from './views/error/404.component';
import { P500Component } from './views/error/500.component';
import { LoginComponent } from './views/login/login.component';
import { AppSettings } from './config/AppSettings';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './views/reset-password/reset-password.component';
import { RequestDemoComponent } from './views/request-demo/request-demo.component';
import { LogoutComponent } from './views/logout/logout.component';
import { CheckLoggedIn } from './service/CheckLoggedIn';
import { CheckLoggedInOuter } from './service/CheckLoggedInOuter';
import { UserComponent } from './views/user/user.component';

export const routes: Routes = [
  {
    path: 'sign-in',
    component: LoginComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Sign In'
    }
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Forgot Password'
    }
  },
  {
    path: 'reset-password/:id',
    component: ResetPasswordComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Reset Password'
    }
  },
  {
    path: 'request-demo',
    component: RequestDemoComponent,
    resolve: { myData: CheckLoggedInOuter },
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Request a Demo'
    }
  },
  {
    path: 'sign-out',
    component: LogoutComponent,
    resolve: { myData: CheckLoggedIn },
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Sign Out'
    }
  },
  {
    path: '',
    redirectTo: 'sign-in',
    resolve: { myData: CheckLoggedInOuter },
    pathMatch: 'full',
  },
  {
    path: '404',
    component: P404Component,
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Page 404'
    }
  },
  {
    path: '500',
    component: P500Component,
    data: {
      title: AppSettings.APP_NAME + ' - ' + 'Page 500'
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
        path: 'dashboard',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/dashboard/dashboard.module').then(m => m.DashboardModule)
      },
      {
        path: 'user',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/user/user.module').then(m => m.UserModule),
      },
      {
        path: 'organisation',
        resolve: { myData: CheckLoggedIn },
        loadChildren: () => import('./views/organisation/organisation.module').then(m => m.OrganisationModule),
      },
      {
        path: 'base',
        loadChildren: () => import('./views/base/base.module').then(m => m.BaseModule)
      },
      {
        path: 'buttons',
        loadChildren: () => import('./views/buttons/buttons.module').then(m => m.ButtonsModule)
      },
      {
        path: 'charts',
        loadChildren: () => import('./views/chartjs/chartjs.module').then(m => m.ChartJSModule)
      },

      {
        path: 'icons',
        loadChildren: () => import('./views/icons/icons.module').then(m => m.IconsModule)
      },
      {
        path: 'notifications',
        loadChildren: () => import('./views/notifications/notifications.module').then(m => m.NotificationsModule)
      },
      {
        path: 'theme',
        loadChildren: () => import('./views/theme/theme.module').then(m => m.ThemeModule)
      },
      {
        path: 'widgets',
        loadChildren: () => import('./views/widgets/widgets.module').then(m => m.WidgetsModule)
      }
    ]
  },
  { path: '**', component: P404Component }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
