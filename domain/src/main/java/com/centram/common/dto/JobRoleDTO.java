package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRoleDTO {
    private BigInteger id;
    private String jobRoleName;
    private BigInteger jobCodeId;
    private BigInteger jobFamilyId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long version;
    private BigInteger modifiedBy;
    private BigInteger createdBy;
}