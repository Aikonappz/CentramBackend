export class AdminDashboard {
    totalCompanies: number;
    activeCompanies: number;
    inactiveCompanies: number;
    allLicenceTypeCompanies: number;
    incidentLicenceTypeCompanies: number;
    assetLicenceTypeCompanies: number;

    constructor() {
        this.totalCompanies = 0;
        this.activeCompanies = 0;
        this.inactiveCompanies = 0;
        this.allLicenceTypeCompanies = 0;
        this.incidentLicenceTypeCompanies = 0;
        this.assetLicenceTypeCompanies = 0;
    }
}
