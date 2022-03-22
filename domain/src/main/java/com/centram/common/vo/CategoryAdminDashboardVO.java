package com.centram.common.vo;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CategoryAdminDashboardVO {
    private Integer aging5;
    private Integer aging10;
    private Integer aging20;
    private Integer aging30;
    private Integer aging60;
    private Set<IncidentPriorityVO> priorityWiseIncidents;
    private Set<IncidentModuleVO> moduleWiseIncidents;
    private Set<IncidentStatusVO> statusWiseIncidents;
    private Set<IncidentPriorityVO> priorityWiseAssetIncidents;
    private Set<IncidentModuleVO> moduleWiseAssetIncidents;
    private Set<IncidentStatusVO> statusWiseAssetIncidents;

    public CategoryAdminDashboardVO(Integer aging5, Integer aging10, Integer aging20, Integer aging30, Integer aging60) {
        this.aging5 = aging5;
        this.aging10 = aging10;
        this.aging20 = aging20;
        this.aging30 = aging30;
        this.aging60 = aging60;
    }
}