import { Base } from "./Base";

export class DistributionListModule extends Base {
    moduleId: number;
    subModuleId: number;

    constructor(mId: number, smId: number) {
        super();
        this.moduleId = mId;
        this.subModuleId = smId;
    }
}