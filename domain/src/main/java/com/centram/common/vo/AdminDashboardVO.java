package com.centram.common.vo;


public interface AdminDashboardVO {
    int getTotalCompanies();
    int getActiveCompanies();
    int getInactiveCompanies();
    int getAllLicenceTypeCompanies();
    int getIncidentLicenceTypeCompanies();
    int getAssetLicenceTypeCompanies();
}