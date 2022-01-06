import { Base } from "./Base";
import { Organisation } from "./Organisation";
import { VendorModule } from "./VendorModule";

export class Vendor extends Base {
    id: number;
    name: string;
    status: any;
    vendorModules: VendorModule[];
    organisation: Organisation;

    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.status = 'ACTIVE';
        this.vendorModules = [];
        this.organisation = new Organisation();
    }
}

export interface VendorList {
    content: Vendor[];
    totalElements: number;
}