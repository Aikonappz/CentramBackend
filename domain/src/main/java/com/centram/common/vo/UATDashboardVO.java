package com.centram.common.vo;


import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UATDashboardVO {
    private Long inProgress;
    private Long completed;
    private Long notStarted;
    private Long total;
}