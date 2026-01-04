package com.centram.common.dto;

import com.centram.domain.Department;
import com.centram.domain.enumarator.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionResponseDto {
    private BigInteger id;

    private String name;

    private String code;

    private String status;

    private LocalDate startDate;

    private String jobCode;

    private BigDecimal fte;

    private Department department;

    private Long locationId;

    private String costCenter;

    private LocalDate endDate;

    private String payGrad;

    private Integer standardHour;

    private Boolean toBeHired;

    private BigDecimal minPay;

    private BigDecimal midPay;

    private BigDecimal maxPay;

    private String recruiterName;

    private BigInteger departmentId;

    private BigInteger organisationId;

    private BigInteger divisionId;

    private BigInteger businessUnitId;

    private String departmentName;

    private String organisationName;

    private String divisionName;

    private String businessUnitName;
    private String locationName;


}
