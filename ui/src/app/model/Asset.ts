import { Base } from "./Base";
export class Asset extends Base {
    id: number;
    productCategory: string;
    assetType: string;
    modelNo: string;
    serialNo: string;
    raisedUser: any;
    isDepartment: boolean;
    location: any;
    department: any;
    raisedForLocation: any;
    isUnderWarranty: boolean;
    warrantyExpiredAt: any;
    purchaseType: any;
    rentalStartAt: any;
    rentalEndAt: any;
    isAvailable: boolean;
    approvedAt: any;
    vendor: any;
    comment: string;
    organisation: any;
    constructor() {
        super();

    }
}

export interface AssetList {
    content: Asset[];
    totalElements: number;
}