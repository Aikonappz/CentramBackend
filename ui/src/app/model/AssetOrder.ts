import { Base } from "./Base";
export class AssetOrder extends Base {
    id: number;
    orderNo: string;
    raisedUser: any;
    isDepartment: boolean;
    location: any;
    department: any;
    currency: string;
    moduleId: number;
    moduleName: string;
    actualModuleName: string;
    subModuleId: number;
    subModuleName: string;
    actualSubModuleName: string;
    model: string;
    quantity: number;
    withinBudget: boolean;
    limitAmount: number;
    extraAmount: number;
    totalAmount: number;
    vendor: any;
    existingAgreement: boolean;
    agreementEndAt: string;
    purchaseType: any;
    rentStartAt: string;
    rentEndAt: string;
    approverUser1: any;
    approvedUser1: boolean;
    approverUser1Comment: string;
    approverUser2: any;
    approvedUser2: boolean;
    approverUser2Comment: string;
    organisation: any;
    constructor() {
        super();
    }
}
export interface AssetOrderList {
    content: AssetOrder[];
    totalElements: number;
}