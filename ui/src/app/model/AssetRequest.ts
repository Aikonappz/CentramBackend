import { Base } from "./Base";
export class AssetRequest extends Base {
    id: number;
    productCategory: string;
    assetType: string;
    modelNo: string;
    longTerm: boolean;
    comment: string;
    approved: boolean;
    approverComment: string;
    allocated: boolean;
    asset: any;
    user: any;
    organisation: any;
    constructor() {
        super();

    }
}

export interface AssetRequestList {
    content: AssetRequest[];
    totalElements: number;
}