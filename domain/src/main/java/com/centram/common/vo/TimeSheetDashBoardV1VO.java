package com.centram.common.vo;


import com.centram.domain.enumarator.ProjectBillingType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TimeSheetDashBoardV1VO implements Serializable {
    private static final long serialVersionUID = 672115467273154434L;
    private Long approvedCount;
    private Long pendingCount;
}