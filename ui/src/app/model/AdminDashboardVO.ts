export class AdminDashboardVO {
    totalCompanies: number;
    activeCompanies: number;
    inactiveCompanies: number;
    allLicenceTypeCompanies: number;
    incidentLicenceTypeCompanies: number;
    assetLicenceTypeCompanies: number;
    projectLicenceTypeCompanies: number;
    uatLicenceTypeCompanies: number;

    constructor() {
        this.totalCompanies = -1;
        this.activeCompanies = -1;
        this.inactiveCompanies = -1;
        this.allLicenceTypeCompanies = -1;
        this.incidentLicenceTypeCompanies = -1;
        this.assetLicenceTypeCompanies = -1;
        this.projectLicenceTypeCompanies = -1;
        this.uatLicenceTypeCompanies = -1;
    }
}
