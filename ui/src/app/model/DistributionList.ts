import { Base } from "./Base";
import { DistributionListModule } from "./DistributionListModule";
import { Organisation } from "./Organisation";

export class DistributionList extends Base {
    id: number;
    dlName: string;
    dlEmail: string;
    distributionListModules: DistributionListModule[];
    organisation: Organisation;

    constructor() {
        super();
        this.id = null;
        this.dlName = '';
        this.dlEmail = '';
        this.distributionListModules = [];
        this.organisation = new Organisation();
    }
}

export interface DistributionListList {
    content: DistributionList[];
    totalElements: number;
}