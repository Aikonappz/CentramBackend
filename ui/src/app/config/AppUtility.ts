import * as moment from "moment-timezone";

export class AppUtility {
    public static LOGGED_IN_PROFILE = 'prfl';
    public static LOGGED_IN_LAST_VISIT = 'lvst';
    public static APP_VIEW_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATE_TIME_FORMAT = 'DD/MM/YYYY hh:mm A';
    public static APP_VIEW_DATEPICKER_INP_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATEPICKER_OP_DATE_FORMAT = "YYYY-MM-DD";
    public static APP_DEFAULT_TIMEZONE = 'Asia/Kolkata';
    public static APP_TIME_FORMAT = 'HH:mm:ss';
    public static APP_CLIENT_STORAGE_TYPE = 'SESSION';
    public static APP_INCIDENT_DRAFT_KEY = 'incidentDraft';
    public static APP_ASSET_DRAFT_KEY = 'assetDraft';
    public static APP_SESSION_TIMEOUT_KEY = 'appSessionTimeout';
    public static APP_LOGGEDIN_USR_ROLES = 'userRoles';
    public static APP_LAST_ACTION_KEY = 'lastAction';
    public static APP_ACTIVITY_CHECK_INTERVAL = 15000;
    public static APP_LOGOUT_WARNING_INTERVAL = 10;
    public static APP_NON_ACTIVITY_LOGOUT_INTERVAL = 20;
    public static MAX_PAGE_SIZE = 2147483647;

    public static EDITOR_CONFIG = {
        readOnly: false,
        toolbar: [
            { name: 'document', groups: ['mode', 'document', 'doctools'], items: ['Source', '-', 'Save', 'NewPage', 'ExportPdf', 'Preview', 'Print', '-', 'Templates'] },
            { name: 'clipboard', groups: ['clipboard', 'undo'], items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo'] },
            { name: 'editing', groups: ['find', 'selection', 'spellchecker'], items: ['Find', 'Replace', '-', 'SelectAll', '-', 'Scayt'] },
            { name: 'forms', items: ['Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField'] },
            '/',
            { name: 'basicstyles', groups: ['basicstyles', 'cleanup'], items: ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat'] },
            { name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi'], items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language'] },
            { name: 'links', items: ['Link', 'Unlink', 'Anchor'] },
            { name: 'insert', items: ['Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe'] },
            '/',
            { name: 'styles', items: ['Styles', 'Format', 'Font', 'FontSize'] },
            { name: 'colors', items: ['TextColor', 'BGColor'] },
            { name: 'tools', items: ['Maximize', 'ShowBlocks'] },
            { name: 'others', items: ['-'] },
            { name: 'about', items: ['About'] }
        ],
        toolbarGroups: [
            { name: 'document', groups: ['mode', 'document', 'doctools'] },
            { name: 'clipboard', groups: ['clipboard', 'undo'] },
            { name: 'editing', groups: ['find', 'selection', 'spellchecker'] },
            { name: 'forms' },
            '/',
            { name: 'basicstyles', groups: ['basicstyles', 'cleanup'] },
            { name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi'] },
            { name: 'links' },
            { name: 'insert' },
            '/',
            { name: 'styles' },
            { name: 'colors' },
            { name: 'tools' },
            { name: 'others' },
            { name: 'about' }
        ]
    };

    constructor() { }

    static prepareDateToString(date: Date): string {
        return String(moment(date).format('YYYY-MM-DD') + 'T00:00:00');
    }

    static getDayHourList(diff: number) {
        return [].concat(...Array.from(Array(24), (_, hour) => ([
            moment({ hour }).format(AppUtility.APP_TIME_FORMAT),
            moment({ hour, minute: diff }).format(AppUtility.APP_TIME_FORMAT)
        ])));
    }

    static getSlaList(end: number) {
        let noList = Array.from(Array(end).keys());
        let timeList;
        noList.forEach(function (part, index) {
            this[index] = String(part).padStart(2, '0') + ":00";;
        }, noList);
        timeList = noList;
        return timeList;
    }

    static toTitleCase(str) {
        return str.replace(
            /\w\S*/g,
            function (txt) {
                return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            }
        );
    }

    static compare(a, b) {
        if (a.last_nom < b.last_nom) {
            return -1;
        }
        if (a.last_nom > b.last_nom) {
            return 1;
        }
        return 0;
    }
}

