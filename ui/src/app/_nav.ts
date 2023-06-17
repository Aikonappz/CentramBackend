import { INavData } from '@coreui/angular';


export const navItems: INavData[] = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    icon: 'icon-speedometer',
    attributes: { "moduleName": "DASHBOARD" }
  },
  {
    name: 'Organizations',
    url: '/organization',
    icon: 'icon-target',
    attributes: { "moduleName": "ORGANIZATION" }
  },
  {
    name: 'Users',
    url: '/user',
    icon: 'icon-people',
    attributes: { "moduleName": "USER" }
  },
  {
    name: 'Permissions',
    url: '/permission',
    icon: 'icon-star',
    attributes: { "moduleName": "ORGANIZATION" }
  },
  {
    name: 'Masters',
    url: '/master',
    icon: 'icon-layers',
    attributes: { "moduleName": "MASTERS" },
    children: [
      {
        name: 'Account',
        url: '/master/account',
        icon: 'cil-object-group',
        attributes: { "moduleName": "ACCOUNT" }
      },
      {
        name: 'Department',
        url: '/master/department',
        icon: 'icon-puzzle',
        attributes: { "moduleName": "DEPARTMENT" }
      },
      {
        name: 'Location',
        url: '/master/location',
        icon: 'icon-location-pin',
        attributes: { "moduleName": "LOCATION" }
      },
      {
        name: 'Priority -Incident',
        url: '/master/priority/incident',
        icon: 'icon-star',
        attributes: { "moduleName": "PRIORITY", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Priority -Asset',
        url: '/master/priority/asset',
        icon: 'icon-star',
        attributes: { "moduleName": "PRIORITY", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Holiday Calendar',
        url: '/master/calendar',
        icon: 'icon-calendar',
        attributes: { "moduleName": "HOLIDAY CALENDAR" }
      },
      {
        name: 'Distribution List',
        url: '/master/dl',
        icon: 'icon-list',
        attributes: { "moduleName": "DISTRIBUTION LIST" }
      },
      {
        name: 'Vendor -Incident',
        url: '/master/vendor/incident',
        icon: 'icon-people',
        attributes: { "moduleName": "VENDOR", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Vendor -Asset',
        url: '/master/vendor/asset',
        icon: 'icon-people',
        attributes: { "moduleName": "VENDOR", "licenceType": "ALL,ASSET" }
      },
    ]
  },
  {
    name: 'Incidents',
    url: '/incident',
    icon: 'fa fa-list-alt',
    attributes: { "moduleName": "INCIDENT" },
    children: [
      {
        name: 'Employee Access',
        title: true,
        url: '/incident/user-route',
        class: 'highlighted-yellow',
        attributes: { "moduleName": "INCIDENT USER", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Create an Incident',
        url: '/incident/user/add/new',
        icon: 'fa fa-plus',
        attributes: { "moduleName": "MY INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'My Incidents',
        url: '/incident/user/all',
        icon: 'fa fa-ticket',
        attributes: { "moduleName": "MY INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Agent Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "moduleName": "INCIDENT AGENT", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'My Group Incidents',
        url: '/incident/agent/all',
        icon: 'fa fa-ticket',
        attributes: { "moduleName": "MY GROUP INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
      {
        name: 'Assigned Incidents',
        url: '/incident/agent/mine',
        icon: 'fa fa-space-shuttle',
        attributes: { "moduleName": "MY GROUP INCIDENTS", "licenceType": "ALL,INCIDENT" }
      },
    ]
  },
  {
    name: 'Asset',
    url: '/asset',
    icon: 'fa fa-cubes',
    attributes: { "moduleName": "ASSET" },
    children: [
      {
        name: 'Admin Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "moduleName": "ASSET ADMIN", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Order an Asset',
        url: '/asset/order',
        icon: 'fa fa-plus',
        attributes: { "moduleName": "ORDER ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Ordered Assets',
        url: '/asset/ordered',
        icon: 'fa fa-indent',
        attributes: { "moduleName": "ORDER ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Inventory Master',
        url: '/asset/inventory',
        icon: 'fa fa-tasks',
        attributes: { "moduleName": "MANAGE ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Pending Approvals',
        url: '/asset/order/incommig',
        icon: 'fa fa-outdent',
        attributes: { "moduleName": "ORDERED ASSET ACTION", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Employee Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "moduleName": "ASSET USER", "licenceType": "ALL,ASSET" }
      },
      {
        name: "Request Asset",
        url: '/asset/user/add/new',
        icon: 'fa fa-plus-square',
        attributes: { "moduleName": "MY ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: "My Assets",
        url: '/asset/allocated',
        icon: 'fa fa-suitcase',
        attributes: { "moduleName": "MY ASSET", "licenceType": "ALL,ASSET" }
      },
      {
        name: "Asset Tickets",
        url: '/asset/requested/outgoing',
        icon: 'fa fa-paper-plane',
        attributes: { "moduleName": "MY ASSET REQUEST", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Pending Approvals',
        url: '/asset/approval/pending',
        icon: 'fa fa-angle-left',
        attributes: { "moduleName": "ASSET REQUEST APPROVAL", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Agent Access',
        title: true,
        class: 'highlighted-yellow',
        attributes: { "moduleName": "ASSET AGENT", "licenceType": "ALL,ASSET" }
      },
      {
        name: 'Asset Tickets',
        url: '/asset/requested/incomming',
        icon: 'fa fa-angle-left',
        attributes: { "moduleName": "REQUESTED ASSET", "licenceType": "ALL,ASSET" }
      },
    ]
  },
  {
    name: 'My Notifications',
    url: '/notification',
    icon: 'icon-info',
    attributes: { "moduleName": "MY NOTIFICATIONS" }
  },
  {
    name: 'Reports',
    url: '/report',
    icon: 'fa fa-folder',
    attributes: { "moduleName": "REPORT" },
    children: [
      {
        name: 'Admin Report',
        url: '/report/admin-report',
        icon: 'fa fa-folder-open',
        attributes: { "moduleName": "SITE ADMIN REPORT" }
      },
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

//attributes: { "moduleName": "ASSET ASIGNENT REPORT", "licenceType": "ALL,ASSET" }
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
