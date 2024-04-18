package com.centram.common.vo;


import com.centram.domain.enumarator.ProjectBillingType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TimeSheetDashBoardVO implements Serializable {
    private static final long serialVersionUID = 672115467273154434L;
    private BigInteger userId;
    private String userName;
    private BigInteger locationId;
    private String locationName;
    private Long noOfWorkingHours;
    private ProjectBillingType projectBillingType;
    private BigInteger projectId;
    private String projectName;
    private LocalDateTime projectStart;
    private LocalDateTime projectEnd;
    private Long submittedTime;
}