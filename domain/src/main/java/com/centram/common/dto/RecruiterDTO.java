package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDTO {
    BigDecimal businessUnitId;
    BigDecimal departmentId;
    BigDecimal divisionId;
    BigDecimal organisationId;
    String position;
}
