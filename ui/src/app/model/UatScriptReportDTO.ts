export class UatScriptReportDTO {
    moduleId: number;
    subModuleId: number;
    no: number;
    projectName: string;
    projectCode: string;
    projectType: string;
    projectCustomer: string;
    projectManager: string;
    consultantResponsible: string;
    technology: string;
    module: string;
    subModule: string;
    testCaseId: string;
    testCaseDescription: string;
    status: string;
    currentlyWith: string;
    age: number;
    constructor() {

    }
}
export interface UatScriptReportDTOList {
    content: UatScriptReportDTO[];
    totalElements: number;
}