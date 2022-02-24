import { Base } from "./Base";
export class AssetOrder extends Base {
    id: number;
    orderNo: string;
    raisedUser: any;
    isDepartment: boolean;
    location: any;
    department: any;
    assetType: any;
    quantity: number;
    cost: number;
    withinBudget: boolean;
    approverUser1: any;
    approvedUser1: boolean;
    approverUser1Comment: string;
    approverUser2: any;
    approvedUser2: boolean;
    approverUser2Comment: string;
    purchaseType: any;
    existingAgreement: boolean;
    vendor: any;
    comment: string;
    organisation: any;
    constructor() {
        super();
    }
}
export interface AssetOrderList {
    content: AssetOrder[];
    totalElements: number;
}