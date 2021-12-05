import * as moment from "moment-timezone";

export class AppUtility {
    public static LOGED_IN_PROFILE = 'LoggedInProfile';
    public static LOGED_IN_PROFILE_JWT = 'LoggedInProfileToken';
    public static LOGED_IN_LAST_VISIT = 'LastVisit';
    public static APP_VIEW_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATE_TIME_FORMAT = 'DD/MM/YYYY HH:mm:ss';
    public static APP_VIEW_DATEPICKER_INP_DATE_FORMAT = 'DD/MM/YYYY';
    public static APP_VIEW_DATEPICKER_OP_DATE_FORMAT = "YYYY-MM-DD";
    public static APP_DEFAULT_TIMEZONE = 'Asia/Kolkata';
    public static APP_TIME_FORMAT = 'HH:mm:ss';

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
        return String(moment(date).format('YYYY-MM-DD') + 'T00:00:00.000Z');
    }

    static appManager(): boolean {
        let loggedInUser: any = JSON.parse(atob(localStorage.getItem(AppUtility.LOGED_IN_PROFILE)));
        //console.log(loggedInUser.appManager);
        return loggedInUser.appManager;
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
}

