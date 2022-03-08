import { Base } from "./Base";
export class Asset extends Base {
    id: number;
    isDepartment: boolean;
    location: any;
    department: any;
    isLocation: boolean;
    raisedForLocation: any;
    moduleId: number;
    moduleName: string;
    actualModuleName: string;
    subModuleId: number;
    subModuleName: string;
    actualSubModuleName: string;
    modelNo: string;
    serialNo: string;
    isUnderWarranty: boolean;
    warrantyExpiredAt: any;
    purchaseType: any;
    rentalStartAt: any;
    rentalEndAt: any;
    isAvailable: boolean;
    vendor: any;
    orderRequestedUser: any;
    approverUser1: any;
    approverUser2: any;
    organisation: any;
    constructor() {
        super();

    }
}

export interface AssetList {
    content: Asset[];
    totalElements: number;
}