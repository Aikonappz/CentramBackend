import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Import Containers
import { DefaultLayoutComponent } from './containers';

import { P404Component } from './views/error/404.component';
import { P500Component } from './views/error/500.component';
import { LoginComponent } from './views/login/login.component';
import { RegisterComponent } from './views/register/register.component';
import {AppSettings} from './configs/AppSettings';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './views/reset-password/reset-password.component';
import { RequestDemoComponent } from './views/request-demo/request-demo.component';

export const routes: Routes = [
  {
    path: 'sign-in',
    component: LoginComponent,
    data: {
      title: AppSettings.APP_NAME+' - '+'Sign In'
    }
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    data: {
      title: AppSettings.APP_NAME+' - '+'Forgot Password'
    }
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    data: {
      title: AppSettings.APP_NAME+' - '+'Reset Password'
    }
  },
  {
    path: 'request-demo',
    component: RequestDemoComponent,
    data: {
      title: AppSettings.APP_NAME+' - '+'Request a Demo'
    }
  },
  {
    path: 'sign-up',
    component: RegisterComponent,
    data: {
      title: AppSettings.APP_NAME+' - '+ 'Sign Up'
    }
  },
  {
    path: '',
    redirectTo: 'sign-in',
    pathMatch: 'full',
  },
  {
    path: '404',
    component: P404Component,
    data: {
      title: AppSettings.APP_NAME+' - '+ 'Page 404'
    }
  },
  {
    path: '500',
    component: P500Component,
    data: {
      title: AppSettings.APP_NAME+' - '+ 'Page 500'
    }
  },
  {
    path: '',
    component: DefaultLayoutComponent,
    data: {
      title: AppSettings.APP_NAME+' - '+ 'Home'
    },
    children: [
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
        path: 'dashboard',
        loadChildren: () => import('./views/dashboard/dashboard.module').then(m => m.DashboardModule)
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
  imports: [ RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' }) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
