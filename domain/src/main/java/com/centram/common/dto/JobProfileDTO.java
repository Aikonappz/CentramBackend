package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobProfileDTO {
    private BigInteger id;
    private BigInteger jobRoleId;
    private List<BigInteger> competencyIds;

    private String rolesAndResponsibilities;
    private String educationBackground;
    private String experienceRequirements;
    private String jobPurpose;
    private String keyRolesAndResponsibilities1;
    private String keyRolesAndResponsibilities2;
    private String keyRolesAndResponsibilities3;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long version;
    private BigInteger modifiedBy;
    private BigInteger createdBy;
}
