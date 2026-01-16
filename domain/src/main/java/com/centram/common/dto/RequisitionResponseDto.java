package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionResponseDto {


    private BigInteger id;

    private String requisitionStatus;

    private String jobTitle;

    private LocalDate jobStartDate;

    private String reasonForVacancy;

    private BigInteger organisationId;

    private BigInteger businessUnitId;

    private BigInteger divisionId;

    private BigInteger departmentId;

    private BigInteger positionId;

    private String jobClassification;

    private BigInteger locationId;

    private BigInteger currencyId;

    private String jobType;

    private String payGrade;

    private BigDecimal payRangeMin;

    private BigDecimal payRangeMid;

    private BigDecimal payRangeMax;

    private BigDecimal approvedBudget;

    private String recruiterName;

    private String hiringManager;

    private String headOfBusinessUnit;

    private String headOfRecruitment;

    private String jobDescription;

    private String interviewingCompetencies;

    private BigDecimal referralBonus;

    private LocalDate jobPostingStartDate;

    private LocalDate jobPostingEndDate;

    private String jobPostingType;

    private String jobPostingBoard;

    private String departmentName;

    private String organisationName;

    private String divisionName;

    private String businessUnitName;

    private String locationName;

    private String jobCode;

    private String notificationStatus;
}
