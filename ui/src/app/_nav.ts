import { INavData } from '@coreui/angular';


export const navItems: INavData[] = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    icon: 'icon-speedometer',
    attributes: { "moduleName": "DASHBOARD" }
  },
  {
    name: 'Organization',
    url: '/organization',
    icon: 'icon-target',
    attributes: { "moduleName": "ORGANIZATION" }
  },
  {
    name: 'User',
    url: '/user',
    icon: 'icon-people',
    attributes: { "moduleName": "USER" }
  },
  {
    name: 'Masters',
    url: '/master',
    icon: 'icon-layers',
    attributes: { "moduleName": "MASTERS" },
    children: [
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
        name: 'Priority',
        url: '/master/priority',
        icon: 'icon-star',
        attributes: { "moduleName": "PRIORITY" }
      },
      {
        name: 'Holiday Calender',
        url: '/master/calender',
        icon: 'icon-calendar',
        attributes: { "moduleName": "HOLIDAY CALENDER" }
      },
      {
        name: 'Distribution List',
        url: '/master/dl',
        icon: 'icon-list',
        attributes: { "moduleName": "DISTRIBUTION LIST" }
      },
      {
        name: 'Vendor',
        url: '/master/vendor',
        icon: 'icon-people',
        attributes: { "moduleName": "VENDOR" }
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
        name: 'Create Incident',
        url: '/incident/add',
        icon: 'fa fa-plus',
        attributes: { "moduleName": "MY INCIDENTS" }
      },
      {
        name: 'My Incidents',
        url: '/incident/user',
        icon: 'fa fa-ticket',
        attributes: { "moduleName": "MY INCIDENTS" }
      },
      {
        name: 'My Group Incidents',
        url: '/incident/agent/all',
        icon: 'fa fa-ticket',
        attributes: { "moduleName": "MY GROUP INCIDENTS" }
      },
      {
        name: 'Assigned Incidents',
        url: '/incident/agent/mine',
        icon: 'fa fa-space-shuttle',
        attributes: { "moduleName": "MY GROUP INCIDENTS" }
      },
    ]
  },
  {
    name: 'My Notifications',
    url: '/notification',
    icon: 'icon-info',
    attributes: { "moduleName": "MY NOTIFICATIONS" }
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
