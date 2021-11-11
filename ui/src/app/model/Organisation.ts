import { Base } from "./Base";
import { LicenseType } from "./enumerator/LicenseType";
import { Status } from "./enumerator/Status";

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
    licenseStart: Date;
    licenseEnd: Date;
    status: Status;
    licenseType: LicenseType;

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
    }
}