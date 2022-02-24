import { Base } from "./Base";
import { MediaFile } from "./MediaFile";
export class AssetRequest extends Base {
    id: number;
    productCategory: string;
    assetType: string;
    modelNo: string;
    longTerm: boolean;
    comment: string;
    approved: boolean;
    requestCanceled: boolean;
    approverComment: string;
    allocated: boolean;
    asset: any;
    user: any;
    attachment: MediaFile;
    organisation: any;
    constructor() {
        super();

    }
}

export interface AssetRequestList {
    content: AssetRequest[];
    totalElements: number;
}