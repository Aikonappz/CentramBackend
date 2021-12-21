import { Base } from "./Base";
import { LicenseType } from "./enumerator/LicenseType";
import { Status } from "./enumerator/Status";
import { MediaFile } from "./MediaFile";
import { Setting } from "./Setting";
import { User } from "./User";

export class Organisation extends Base {
    id: number;
    name: string;
    add1: string;
    add2: string;
    city: string;
    pincode: string;
    pan: string;
    tan: string;
    gstin: string;
    licenseStart: any;
    licenseEnd: any;
    status: any;
    licenseType: LicenseType;
    setting: Setting;

    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.add1 = '';
        this.add2 = '';
        this.city = '';
        this.pincode = '';
        this.pan = '';
        this.tan = '';
        this.gstin = '';
        this.licenseStart = null;
        this.licenseEnd = null;
        this.status = Status.ACTIVE;
        this.licenseType = LicenseType.ALL;
        this.setting = new Setting();
    }
}


export interface OrganisationList {
    content: Organisation[];
    totalElements: number;
}