import * as moment from "moment-timezone";

export class AppUtility {
    public static APP_URL = 'https://www.google.com';
    public static APP_BRAND_NAME = 'Centram';
    public static APP_DEV_NAME = 'Centram Dev';
    public static API_ENDPOINT = 'http://localhost:7001/api';
    public static LANDING_PAGE = '/';
    public static APP_NAME = 'Centram';
    public static LOGED_IN_PROFILE = 'LoggedInProfile';
    public static LOGED_IN_USER_PERMISSIONS = 'LoggedInUserPermissions';
    public static LOGED_IN_PROFILE_JWT = 'LoggedInProfileToken';
    public static LOGED_IN_LAST_VISIT = 'LastVisit';
    public static APP_VIEW_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATE_TIME_FORMAT = 'DD/MM/YYYY HH:mm:ss';
    public static APP_VIEW_DATEPICKER_INP_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATEPICKER_OP_DATE_FORMAT = "YYYY-MM-DD";
    public static APP_DEFAULT_TIMEZONE = 'Asia/Kolkata';
    public static APP_TIME_FORMAT = 'HH:mm:ss';

    static prepareDateToString(date: Date): string {
        return String(moment(date).format('YYYY-MM-DD') + 'T00:00:00.000Z');
    }

    static appManager(): boolean {
        let loggedInUser: any = JSON.parse(localStorage.getItem(AppUtility.LOGED_IN_PROFILE));
        //console.log(loggedInUser.appManager);
        return loggedInUser.appManager;
    }

    static getDayHourList(diff: number) {
        return [].concat(...Array.from(Array(24), (_, hour) => ([
            moment({ hour }).format(AppUtility.APP_TIME_FORMAT),
            moment({ hour, minute: diff }).format(AppUtility.APP_TIME_FORMAT)
        ])));
    }

}

