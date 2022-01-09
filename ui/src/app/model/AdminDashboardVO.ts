export class AdminDashboardVO {
    totalCompanies: number;
    activeCompanies: number;
    inactiveCompanies: number;
    allLicenceTypeCompanies: number;
    incidentLicenceTypeCompanies: number;
    assetLicenceTypeCompanies: number;

    constructor() {
        this.totalCompanies = -1;
        this.activeCompanies = -1;
        this.inactiveCompanies = -1;
        this.allLicenceTypeCompanies = -1;
        this.incidentLicenceTypeCompanies = -1;
        this.assetLicenceTypeCompanies = -1;
    }
}
