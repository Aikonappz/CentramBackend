package com.centram.common.vo;


import com.centram.domain.enumarator.ProjectBillingType;
import com.centram.domain.enumarator.ProjectType;
import com.centram.domain.enumarator.Technology;

import java.math.BigInteger;
import java.time.LocalDateTime;

public interface AllocationDetailVO {
    Technology getTechnology();
    ProjectType getProjectType();
    ProjectBillingType getProjectBillingType();
    String getModuleName();
    String getSubModuleName();
    String getName();
    String getCode();
    LocalDateTime getStart();
    LocalDateTime getEnd();
    String getMaxAllocation();
    LocalDateTime getAllocatedAt();
    LocalDateTime getDeallocatedAt();
    Boolean getDeallocated();
    String getUserName();
    String getUserEmail();
    LocalDateTime getAllocationStart();
    LocalDateTime getAllocationEnd();
}