package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlankRequisitionRequestDto {

    private BigInteger id;
    private String jobTitle;
    private BigInteger organisationId;
    private BigInteger businessUnitId;
    private BigInteger divisionId;
    private BigInteger departmentId;
    private Long locationId;
    private String jobDescription;
    private String recruiter;
    private String payGrade;
    private BigDecimal payRangeMin;
    private BigDecimal payRangeMid;
    private BigDecimal payRangeMax;
    private String jobCode;
    private String hiringManager;
    private String headOfBusinessUnit;
    private String headOfRecruitment;
    private String notificationStatus;
    private String requisitionStatus;
    private String positionName;
    private LocalDate jobPostingStartDate;
    private LocalDate jobPostingEndDate;
}
