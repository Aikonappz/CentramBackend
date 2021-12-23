// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  appUrl: 'https://www.google.com',
  appBrandName: 'Centram',
  appDevName: 'Centram Dev',
  appLandingPage: '/',
  appName: 'Centram',
  appServiceEndpoint: 'http://localhost:7001/api',
  appWSServiceEndpoint: 'http://localhost:7001/app-ws-notification',
  appWSNotificationTopic: '/topic/notification',
  appWSCred: {
    "app_ws_client_shared_user": "YXBwQ2VudHJhbVdzVXNlcg==",
    "app_ws_client_shared_pass": "YXBwQ2VudHJhbVdzVXNlckAjOTA4Nw=="
  }
};

// export const environment = {
//   production: false,
//   appUrl: 'https://www.google.com',
//   appBrandName: 'Centram',
//   appDevName: 'Centram Dev',
//   appLandingPage: '/',
//   appName: 'Centram',
//   appServiceEndpoint: 'http://3.111.47.178:7001/api',
//   appWSServiceEndpoint: 'http://3.111.47.178:7001/app-ws-notification',
//   appWSNotificationTopic: '/topic/notification',
//   appWSCred: {
//     "app_ws_client_shared_user": "YXBwQ2VudHJhbVdzVXNlcg==",
//     "app_ws_client_shared_pass": "YXBwQ2VudHJhbVdzVXNlckAjOTA4Nw=="
//   }
// };