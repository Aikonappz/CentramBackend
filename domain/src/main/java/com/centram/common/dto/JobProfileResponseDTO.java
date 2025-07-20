package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobProfileResponseDTO {
    private BigInteger id;
    private String rolesAndResponsibilities;
    private String educationBackground;
    private String experienceRequirements;
    private String jobPurpose;
    private String keyRolesAndResponsibilities1;
    private String keyRolesAndResponsibilities2;
    private String keyRolesAndResponsibilities3;

    private BigInteger jobRoleId;
    private String jobRoleName;
    private BigInteger jobCodeId;

    private List<CompetencyBasicDTO> competencies;
}
