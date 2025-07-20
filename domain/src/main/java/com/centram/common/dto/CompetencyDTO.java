package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyDTO {
    private BigInteger id;
    private String competencyName;
    private BigInteger jobRoleId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long version;
    private BigInteger modifiedBy;
    private BigInteger createdBy;
}
