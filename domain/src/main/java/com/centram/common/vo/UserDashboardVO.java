package com.centram.common.vo;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDashboardVO {
    private Set<IncidentModuleVO> moduleWiseIncidents;
    private Set<IncidentStatusVO> statusWiseIncidents;
    private Set<IncidentModuleVO> moduleWiseAssetIncidents;
    private Set<IncidentStatusVO> statusWiseAssetIncidents;
}