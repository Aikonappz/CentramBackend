import * as moment from "moment-timezone";

export class AppSettings {
    public static APP_URL = 'https://www.google.com';
    public static APP_BRAND_NAME = 'Centram';
    public static APP_DEV_NAME = 'Centram Dev';
    public static API_ENDPOINT = 'http://localhost:7001/api';
    public static LANDING_PAGE = '/';
    public static APP_NAME = 'Centram';
    public static LOGED_IN_PROFILE = 'LoggedInProfile';
    public static LOGED_IN_PROFILE_JWT = 'LoggedInProfileToken';
    public static LOGED_IN_LAST_VISIT = 'LastVisit';
    public static APP_VIEW_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATE_TIME_FORMAT = 'DD/MM/YYYY HH:mm:ss';
    public static APP_VIEW_DATEPICKER_INP_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATEPICKER_OP_DATE_FORMAT = "YYYY-MM-DD";
    public static APP_DEFAULT_TIMEZONE = 'Asia/Kolkata';

    static prepareDateToString(date: Date): string {
        //console.log(moment('05-06-2018', 'MM-DD-YYYY').format('DD-MM-YY'));
        // alternatively (and preferrably) this:
        //console.log(moment.utc('05-06-2018', 'MM-DD-YYYY').format('DD-MM-YY'));
        //console.log(moment(date).format('YYYY-MM-DD')+'T00:00:00.000Z');
        return String(moment(date).format('YYYY-MM-DD') + 'T00:00:00.000Z');
    }
}

