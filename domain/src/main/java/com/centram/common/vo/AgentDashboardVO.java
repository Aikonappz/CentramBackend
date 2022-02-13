package com.centram.common.vo;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AgentDashboardVO {
    private Set<IncidentPriorityVO> priorityWiseIncidents;
    private Set<IncidentModuleVO> moduleWiseIncidents;
    private Set<IncidentStatusVO> statusWiseIncidents;
}