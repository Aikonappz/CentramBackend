import { INavData } from '@coreui/angular';


export const navItems: INavData[] = [
  // SUPER ADMIN
  {
    name: 'Dashboard',
    url: '/supadmin/dashboard',
    icon: 'icon-speedometer',
    attributes: { "parentModule": "SUPADMIN", "moduleName": "DASHBOARD", "licenceType": "SUPADMIN" }
  },
  {
    name: 'Organizations',
    url: '/supadmin/organization',
    icon: 'icon-target',
    attributes: { "parentModule": "SUPADMIN", "moduleName": "ORGANIZATION", "licenceType": "SUPADMIN" }
  },
  {
    name: 'Users',
    url: '/supadmin/user',
    icon: 'icon-people',
    attributes: { "parentModule": "SUPADMIN", "moduleName": "USER", "licenceType": "SUPADMIN" }
  },
  {
    name: 'Permissions',
    url: '/supadmin/permission',
    icon: 'icon-star',
    attributes: { "parentModule": "SUPADMIN", "moduleName": "ORGANIZATION", "licenceType": "SUPADMIN" }
  },
  {
    name: 'Reports',
    url: '/supadmin/report',
    icon: 'fa fa-folder',
    attributes: { "parentModule": "SUPADMIN", "moduleName": "REPORT", "licenceType": "SUPADMIN" },
    children: [
      {
        name: 'Admin Report',
        url: '/supadmin/report/admin-report',
        icon: 'fa fa-folder-open',
        attributes: { "parentModule": "SUPADMIN", "moduleName": "SITE ADMIN REPORT", "licenceType": "SUPADMIN" }
      },
    ]
  },
  // SUPER ADMIN
  // ORG ADMIN
  {
    name: 'Dashboard',
    url: '/admin/dashboard',
    icon: 'icon-speedometer',
    attributes: { "parentModule": "ADMIN", "moduleName": "DASHBOARD", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" }
  },
  {
    name: 'Users',
    url: '/admin/user',
    icon: 'icon-people',
    attributes: { "parentModule": "ADMIN", "moduleName": "USER", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" }
  },
  {
    name: 'Masters',
    url: '/admin/master',
    icon: 'icon-layers',
    attributes: { "parentModule": "ADMIN", "moduleName": "MASTERS", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" },
    children: [
      {
        name: 'Account',
        url: '/admin/master/account',
        icon: 'cil-object-group',
        attributes: { "parentModule": "ADMIN", "moduleName": "ACCOUNT", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" }
      },
      {
        name: 'Department',
        url: '/admin/master/department',
        icon: 'icon-puzzle',
        attributes: { "parentModule": "ADMIN", "moduleName": "DEPARTMENT", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" }
      },
      {
        name: 'Location',
        url: '/admin/master/location',
        icon: 'icon-location-pin',
        attributes: { "parentModule": "ADMIN", "moduleName": "LOCATION", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" }
      },
      {
        name: 'Priority -Incident',
        url: '/admin/master/priority/incident',
        icon: 'icon-star',
        attributes: { "parentModule": "ADMIN", "moduleName": "PRIORITY", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Priority -Asset',
        url: '/admin/master/priority/asset',
        icon: 'icon-star',
        attributes: { "parentModule": "ADMIN", "moduleName": "PRIORITY", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Holiday Calendar',
        url: '/admin/master/calendar',
        icon: 'icon-calendar',
        attributes: { "parentModule": "ADMIN", "moduleName": "HOLIDAY CALENDAR", "licenceType": "ALL,INCIDENT,ASSET" }
      },
      {
        name: 'Distribution List',
        url: '/admin/master/dl',
        icon: 'icon-list',
        attributes: { "parentModule": "ADMIN", "moduleName": "DISTRIBUTION LIST", "licenceType": "ALL,INCIDENT,ASSET" }
      },
      {
        name: 'Project Master',
        url: '/admin/master/project',
        icon: 'cil-blur-linear',
        attributes: { "parentModule": "ADMIN", "moduleName": "PROJECT_MASTER", "licenceType": "ALL,PROJECT,UAT" }
      },
      {
        name: 'Vendor -Incident',
        url: '/admin/master/vendor/incident',
        icon: 'icon-people',
        attributes: { "parentModule": "ADMIN", "moduleName": "VENDOR", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Vendor -Asset',
        url: '/admin/master/vendor/asset',
        icon: 'icon-people',
        attributes: { "parentModule": "ADMIN", "moduleName": "VENDOR", "licenceType": "ALL,ASSET" }
      },
    ]
  },
  {
    name: 'Reports',
    url: '/admin/report',
    icon: 'fa fa-folder',
    attributes: { "parentModule": "ADMIN", "moduleName": "REPORT", "licenceType": "ALL,INCIDENT,ASSET,PROJECT,UAT" },
    children: [
      {
        name: 'Incident Report',
        url: '/admin/report/incident-report',
        icon: 'fa fa-file',
        attributes: { "parentModule": "ADMIN", "moduleName": "INCIDENT REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Escalation Report',
        url: '/admin/report/escalation-report',
        icon: 'fa fa-file-excel-o',
        attributes: { "parentModule": "ADMIN", "moduleName": "ESCALATION REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Reopened Report',
        url: '/admin/report/reopen-report',
        icon: 'fa fa-folder-open',
        attributes: { "parentModule": "ADMIN", "moduleName": "REOPEN REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Aging Report',
        url: '/admin/report/aging-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "AGING REPORT", "licenceType": "ALL,INCIDENT" }
      },
      // {
      //   name: 'Asset Report',
      //   url: '/report/asset-order-report',
      //   class: 'highlighted-yellow',
      //   attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,INCIDENT" }
      // },
      // {
      //   name: 'Order Report',
      //   url: '/report/asset-order-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,ASSET" }
      // },
      // {
      //   name: 'Allocation Report',
      //   url: '/report/asset-allocation-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "ALLOCATION REPORT", "licenceType": "ALL,ASSET" }
      // },
      // {
      //   name: 'Ticket Report',
      //   url: '/report/asset-ticket-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "TICKETS REPORT", "licenceType": "ALL,ASSET" }
      // },
      {
        name: 'Vendor Report',
        url: '/admin/report/vendor-report/asset',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "VENDOR REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Order Report',
        url: '/admin/report/order-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "ORDER REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Tickets Report',
        url: '/admin/report/asset-tickets-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "ASSET TICKET REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Assignment Report',
        url: '/admin/report/asset-assignment-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "ASSET ASSIGNMENT REPORT", "licenceType": "ALL,ASSET" }
      },
    ]
  },
  // ORG ADMIN
  // UAT
  {
    name: 'Dashboard',
    url: '/uat/dashboard',
    icon: 'icon-speedometer',
    attributes: { "parentModule": "UAT", "moduleName": "DASHBOARD", "licenceType": "ALL,UAT" }
  },
  {
    name: 'UAT',
    url: '/uat/activities',
    icon: 'fa fa-flask',
    attributes: { "parentModule": "UAT", "moduleName": "UAT", "licenceType": "ALL,UAT" },
    children: [
      {
        name: 'UAT Activities',
        url: '/uat/activities',
        icon: 'fa fa-suitcase',
        attributes: { "parentModule": "UAT", "moduleName": "UAT ACTIVITIES", "licenceType": "ALL,UAT" }
      },
    ]
  },
  {
    name: 'Reports',
    icon: 'fa fa-folder',
    url: '/uat/report',
    attributes: { "parentModule": "UAT", "moduleName": "REPORT", "licenceType": "ALL,UAT" },
    children: [
      {
        name: 'UAT Report',
        url: '/uat/report',
        icon: 'fa fa-folder-open',
        attributes: { "parentModule": "UAT", "moduleName": "SITE ADMIN REPORT", "licenceType": "ALL,UAT" }
      },
    ]
  },
  // UAT
  // INCIDENT
  {
    name: 'Dashboard',
    url: '/incident/dashboard',
    icon: 'icon-speedometer',
    attributes: { "parentModule": "INCIDENT", "moduleName": "DASHBOARD", "licenceType": "ALL,INCIDENT" }
  },
  {
    name: 'Incidents',
    url: '/incident',
    icon: 'fa fa-list-alt',
    attributes: { "parentModule": "INCIDENT", "moduleName": "INCIDENT", "licenceType": "ALL,INCIDENT" },
    children: [
      {
        name: 'Employee Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "parentModule": "INCIDENT", "moduleName": "INCIDENT USER", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Create an Incident',
        url: '/incident/user/add/new',
        icon: 'fa fa-plus',
        attributes: { "parentModule": "INCIDENT", "moduleName": "MY INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'My Incidents',
        url: '/incident/user/all',
        icon: 'fa fa-ticket',
        attributes: { "parentModule": "INCIDENT", "moduleName": "MY INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Agent Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "parentModule": "INCIDENT", "moduleName": "INCIDENT AGENT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'My Group Incidents',
        url: '/incident/agent/all',
        icon: 'fa fa-ticket',
        attributes: { "parentModule": "INCIDENT", "moduleName": "MY GROUP INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Assigned Incidents',
        url: '/incident/agent/mine',
        icon: 'fa fa-space-shuttle',
        attributes: { "parentModule": "INCIDENT", "moduleName": "MY GROUP INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
    ]
  },
  {
    name: 'Reports',
    icon: 'fa fa-folder',
    url: '/incident/report',
    attributes: { "parentModule": "INCIDENT", "moduleName": "REPORT", "licenceType": "ALL,INCIDENT" },
    children: [
      {
        name: 'Incident Report',
        url: '/incident/report/incident-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "INCIDENT", "moduleName": "INCIDENT REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Escalation Report',
        url: '/incident/report/escalation-report',
        icon: 'fa fa-file-excel-o',
        attributes: { "parentModule": "INCIDENT", "moduleName": "ESCALATION REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Reopened Report',
        url: '/incident/report/reopen-report',
        icon: 'fa fa-folder-open',
        attributes: { "parentModule": "INCIDENT", "moduleName": "REOPEN REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Aging Report',
        url: '/incident/report/aging-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "INCIDENT", "moduleName": "AGING REPORT", "licenceType": "ALL,INCIDENT" }
      },
    ]
  },
  // INCIDENT
  // ASSET
  {
    name: 'Dashboard',
    url: '/asset/dashboard',
    icon: 'icon-speedometer',
    attributes: { "parentModule": "ASSET", "moduleName": "DASHBOARD", "licenceType": "ALL,ASSET" }
  },
  {
    name: 'Asset',
    url: '/asset',
    icon: 'fa fa-cubes',
    attributes: { "parentModule": "ASSET", "moduleName": "ASSET", "licenceType": "ALL,ASSET" },
    children: [
      {
        name: 'Admin Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "parentModule": "ASSET", "moduleName": "ASSET ADMIN", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Order an Asset',
        url: '/asset/order',
        icon: 'fa fa-plus',
        attributes: { "parentModule": "ASSET", "moduleName": "ORDER ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Ordered Assets',
        url: '/asset/ordered',
        icon: 'fa fa-indent',
        attributes: { "parentModule": "ASSET", "moduleName": "ORDER ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Inventory Master',
        url: '/asset/inventory',
        icon: 'fa fa-tasks',
        attributes: { "parentModule": "ASSET", "moduleName": "MANAGE ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Pending Approvals',
        url: '/asset/order/incommig',
        icon: 'fa fa-outdent',
        attributes: { "parentModule": "ASSET", "moduleName": "ORDERED ASSET ACTION", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Employee Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "parentModule": "ASSET", "moduleName": "ASSET USER", "licenceType": "ALL,ASSET" }
      },
      {
        name: "Request Asset",
        url: '/asset/user/add/new',
        icon: 'fa fa-plus-square',
        attributes: { "parentModule": "ASSET", "moduleName": "MY ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: "My Assets",
        url: '/asset/allocated',
        icon: 'fa fa-suitcase',
        attributes: { "parentModule": "ASSET", "moduleName": "MY ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: "Asset Tickets",
        url: '/asset/requested/outgoing',
        icon: 'fa fa-paper-plane',
        attributes: { "parentModule": "ASSET", "moduleName": "MY ASSET REQUEST", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Pending Approvals',
        url: '/asset/approval/pending',
        icon: 'fa fa-angle-left',
        attributes: { "parentModule": "ASSET", "moduleName": "ASSET REQUEST APPROVAL", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Agent Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "parentModule": "ASSET", "moduleName": "ASSET AGENT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Tickets',
        url: '/asset/requested/incomming',
        icon: 'fa fa-angle-left',
        attributes: { "parentModule": "ASSET", "moduleName": "REQUESTED ASSET", "licenceType": "ALL,ASSET" }
      },
    ]
  },
  {
    name: 'Reports',
    icon: 'fa fa-folder',
    url: '/asset/report',
    attributes: { "parentModule": "ASSET", "moduleName": "REPORT", "licenceType": "ALL,ASSET" },
    children: [
      // {
      //   name: 'Asset Report',
      //   url: '/report/asset-order-report',
      //   class: 'highlighted-yellow',
      //   attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,INCIDENT" }
      // },
      // {
      //   name: 'Order Report',
      //   url: '/report/asset-order-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,ASSET" }
      // },
      // {
      //   name: 'Allocation Report',
      //   url: '/report/asset-allocation-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "ALLOCATION REPORT", "licenceType": "ALL,ASSET" }
      // },
      // {
      //   name: 'Ticket Report',
      //   url: '/report/asset-ticket-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "TICKETS REPORT", "licenceType": "ALL,ASSET" }
      // },
      {
        name: 'Vendor Report',
        url: '/asset/report/vendor-report/asset',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "VENDOR REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Order Report',
        url: '/asset/report/order-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "ORDER REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Tickets Report',
        url: '/asset/report/asset-tickets-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "ASSET TICKET REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Assignment Report',
        url: '/asset/report/asset-assignment-report',
        icon: 'fa fa-file-text',
        attributes: { "parentModule": "ADMIN", "moduleName": "ASSET ASSIGNMENT REPORT", "licenceType": "ALL,ASSET" }
      },
    ]
  },
  // INCIDENT



  // NOTIFICATIONS COMMON FOR ALL
  {
    name: 'My Notifications',
    url: '/notification',
    icon: 'icon-info',
    attributes: { "moduleName": "MY NOTIFICATIONS", "commonModule": true }
  },
  // NOTIFICATIONS COMMON FOR ALL


  {
    name: 'Project',
    url: '/project',
    icon: 'fa fa-list-alt',
    attributes: { "moduleName": "PROJECT", "licenceType": "ALL,PROJECT" },
    children: [
      {
        name: 'Manage Timesheet',
        url: '/project/manage-timesheet',
        icon: 'cil-clock',
        attributes: { "moduleName": "MANAGE TIMESHEET", "licenceType": "ALL,PROJECT" }
      },
      {
        name: 'Allocate Project',
        url: '/project/allocate',
        icon: 'cil-grain',
        attributes: { "moduleName": "ALLOCATE PROJECT", "licenceType": "ALL,PROJECT" }
      },
      {
        name: 'Deallocate Project',
        url: '/project/deallocate',
        icon: 'cil-gradient',
        attributes: { "moduleName": "DEALLOCATE PROJECT", "licenceType": "ALL,PROJECT" }
      },
    ]
  },

  {
    name: 'Reports',
    url: '/report',
    icon: 'fa fa-folder',
    attributes: { "moduleName": "REPORT" },
    children: [
      {
        name: 'Incident Report',
        url: '/report/incident-report',
        class: 'highlighted-yellow',
        attributes: { "moduleName": "INCIDENT USER", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Incident Report',
        url: '/report/incident-report',
        icon: 'fa fa-file',
        attributes: { "moduleName": "INCIDENT REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Escalation Report',
        url: '/report/escalation-report',
        icon: 'fa fa-file-excel-o',
        attributes: { "moduleName": "ESCALATION REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Reopened Report',
        url: '/report/reopen-report',
        icon: 'fa fa-folder-open',
        attributes: { "moduleName": "REOPEN REPORT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Aging Report',
        url: '/report/aging-report',
        icon: 'fa fa-file-text',
        attributes: { "moduleName": "AGING REPORT", "licenceType": "ALL,INCIDENT" }
      },
      // {
      //   name: 'Asset Report',
      //   url: '/report/asset-order-report',
      //   class: 'highlighted-yellow',
      //   attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,INCIDENT" }
      // },
      // {
      //   name: 'Order Report',
      //   url: '/report/asset-order-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,ASSET" }
      // },
      // {
      //   name: 'Allocation Report',
      //   url: '/report/asset-allocation-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "ALLOCATION REPORT", "licenceType": "ALL,ASSET" }
      // },
      // {
      //   name: 'Ticket Report',
      //   url: '/report/asset-ticket-report',
      //   icon: 'fa fa-file-text',
      //   attributes: { "moduleName": "TICKETS REPORT", "licenceType": "ALL,ASSET" }
      // },
      {
        name: 'Vendor Report',
        url: '/report/vendor-report/asset',
        icon: 'fa fa-file-text',
        attributes: { "moduleName": "VENDOR REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Order Report',
        url: '/report/order-report',
        icon: 'fa fa-file-text',
        attributes: { "moduleName": "ORDER REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Tickets Report',
        url: '/report/asset-tickets-report',
        icon: 'fa fa-file-text',
        attributes: { "moduleName": "ASSET TICKET REPORT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Assignment Report',
        url: '/report/asset-assignment-report',
        icon: 'fa fa-file-text',
        attributes: { "moduleName": "ASSET ASSIGNMENT REPORT", "licenceType": "ALL,ASSET" }
      },
    ]
  },
];

/*export const navItems: INavData[] = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    icon: 'icon-speedometer',
  },
  {
    name: 'Organisation',
    url: '/dashboard',
    icon: 'icon-speedometer',
  },
  {
    name: 'User',
    url: '/dashboard',
    icon: 'icon-speedometer',
  },
  {
    title: true,
    name: 'Theme'
  },
  {
    name: 'Colors',
    url: '/theme/colors',
    icon: 'icon-drop'
  },
  {
    name: 'Typography',
    url: '/theme/typography',
    icon: 'icon-pencil'
  },
  {
    title: true,
    name: 'Components'
  },
  {
    name: 'Base',
    url: '/base',
    icon: 'icon-puzzle',
    children: [
      {
        name: 'Cards',
        url: '/base/cards',
        icon: 'icon-puzzle'
      },
      {
        name: 'Carousels',
        url: '/base/carousels',
        icon: 'icon-puzzle'
      },
      {
        name: 'Collapses',
        url: '/base/collapses',
        icon: 'icon-puzzle'
      },
      {
        name: 'Forms',
        url: '/base/forms',
        icon: 'icon-puzzle'
      },
      {
        name: 'Navbars',
        url: '/base/navbars',
        icon: 'icon-puzzle'

      },
      {
        name: 'Pagination',
        url: '/base/paginations',
        icon: 'icon-puzzle'
      },
      {
        name: 'Popovers',
        url: '/base/popovers',
        icon: 'icon-puzzle'
      },
      {
        name: 'Progress',
        url: '/base/progress',
        icon: 'icon-puzzle'
      },
      {
        name: 'Switches',
        url: '/base/switches',
        icon: 'icon-puzzle'
      },
      {
        name: 'Tables',
        url: '/base/tables',
        icon: 'icon-puzzle'
      },
      {
        name: 'Tabs',
        url: '/base/tabs',
        icon: 'icon-puzzle'
      },
      {
        name: 'Tooltips',
        url: '/base/tooltips',
        icon: 'icon-puzzle'
      }
    ]
  },
  {
    name: 'Buttons',
    url: '/buttons',
    icon: 'icon-cursor',
    children: [
      {
        name: 'Buttons',
        url: '/buttons/buttons',
        icon: 'icon-cursor'
      },
      {
        name: 'Dropdowns',
        url: '/buttons/dropdowns',
        icon: 'icon-cursor'
      },
      {
        name: 'Brand Buttons',
        url: '/buttons/brand-buttons',
        icon: 'icon-cursor'
      }
    ]
  },
  {
    name: 'Charts',
    url: '/charts',
    icon: 'icon-pie-chart'
  },
  {
    name: 'Icons',
    url: '/icons',
    icon: 'icon-star',
    children: [
      {
        name: 'CoreUI Icons',
        url: '/icons/coreui-icons',
        icon: 'icon-star',
        badge: {
          variant: 'success',
          text: 'NEW'
        }
      },
      {
        name: 'Flags',
        url: '/icons/flags',
        icon: 'icon-star'
      },
      {
        name: 'Font Awesome',
        url: '/icons/font-awesome',
        icon: 'icon-star',
        badge: {
          variant: 'secondary',
          text: '4.7'
        }
      },
      {
        name: 'Simple Line Icons',
        url: '/icons/simple-line-icons',
        icon: 'icon-star'
      }
    ]
  },
  {
    name: 'Notifications',
    url: '/notifications',
    icon: 'icon-bell',
    children: [
      {
        name: 'Alerts',
        url: '/notifications/alerts',
        icon: 'icon-bell'
      },
      {
        name: 'Badges',
        url: '/notifications/badges',
        icon: 'icon-bell'
      },
      {
        name: 'Modals',
        url: '/notifications/modals',
        icon: 'icon-bell'
      }
    ]
  },
  {
    name: 'Widgets',
    url: '/widgets',
    icon: 'icon-calculator',
    badge: {
      variant: 'info',
      text: 'NEW'
    }
  },
  {
    divider: true
  },
  {
    title: true,
    name: 'Extras',
  },
  {
    name: 'Pages',
    url: '/pages',
    icon: 'icon-star',
    children: [
      {
        name: 'Login',
        url: '/login',
        icon: 'icon-star'
      },
      {
        name: 'Register',
        url: '/register',
        icon: 'icon-star'
      },
      {
        name: 'Error 404',
        url: '/404',
        icon: 'icon-star'
      },
      {
        name: 'Error 500',
        url: '/500',
        icon: 'icon-star'
      }
    ]
  },
  {
    name: 'Disabled',
    url: '/dashboard',
    icon: 'icon-ban',
    badge: {
      variant: 'secondary',
      text: 'NEW'
    },
    attributes: { disabled: true },
  },
  {
    name: 'Download CoreUI',
    url: 'http://coreui.io/angular/',
    icon: 'icon-cloud-download',
    class: 'mt-auto',
    variant: 'success',
    attributes: { target: '_blank', rel: 'noopener' }
  },
  {
    name: 'Try CoreUI PRO',
    url: 'http://coreui.io/pro/angular/',
    icon: 'icon-layers',
    variant: 'danger',
    attributes: { target: '_blank', rel: 'noopener' }
  }
];*/
