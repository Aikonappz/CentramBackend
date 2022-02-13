package com.centram.common.vo;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class OrgAdminDashboardVO {
    private long activeEmployees;
    private long inHouseVendors;
    private long outSourcedVendors;
    private Set<IncidentModuleVO> moduleWiseIncidents;
    private Set<IncidentStatusVO> statusWiseIncidents;

    public OrgAdminDashboardVO(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public OrgAdminDashboardVO(long inHouseVendors, long outSourcedVendors) {
        this.inHouseVendors = inHouseVendors;
        this.outSourcedVendors = outSourcedVendors;
    }

    public OrgAdminDashboardVO(long activeEmployees, long inHouseVendors, long outSourcedVendors) {
        this.activeEmployees = activeEmployees;
        this.inHouseVendors = inHouseVendors;
        this.outSourcedVendors = outSourcedVendors;
    }
}