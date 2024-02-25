export class AllocationDetailVO {
    technology: any;
    projectType: any;
    projectBillingType: any;
    moduleName: any;
    subModuleName: any;
    name: any;
    code: any;
    start: any;
    end: any;
    maxAllocation: any;
    allocatedAt: any;
    deallocatedAt: any;
    deallocated: any;
    userName: any;
    userEmail: any;
    allocationStart: any;
    allocationEnd: any;
    constructor() {

    }
}
export interface AllocationDetailVOList {
    content: AllocationDetailVO[];
    totalElements: number;
}