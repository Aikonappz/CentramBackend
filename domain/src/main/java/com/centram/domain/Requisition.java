package com.centram.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "requisition",
        uniqueConstraints = @UniqueConstraint(name = "requisition_constraint", columnNames = {"job_title", "organisation_id"}),
        indexes = {
                @Index(name = "requisition_organisation_id_idx", columnList = "organisation_id", unique = false),
        }
)
public class Requisition extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "requisition_status", columnDefinition = "VARCHAR(255) NOT NULL")
    private String requisitionStatus;

    @Column(name = "job_title", columnDefinition = "VARCHAR(255) NOT NULL")
    private String jobTitle;

    @Column(name = "job_start_date")
    private LocalDate jobStartDate;

    @Column(name = "reason_for_vacancy", columnDefinition = "VARCHAR(255)")
    private String reasonForVacancy;

    @Column(name = "organisation_id")
    private BigInteger organisationId;

    @Column(name = "business_unit_id")
    private BigInteger businessUnitId;

    @Column(name = "division_id")
    private BigInteger divisionId;

    @Column(name = "department_id")
    private BigInteger departmentId;

    @Column(name = "position_id")
    private BigInteger positionId;

    @Column(name = "job_classification", columnDefinition = "VARCHAR(255)")
    private String jobClassification;

    @Column(name = "location_id")
    private BigInteger locationId;

    @Column(name = "currency_id")
    private BigInteger currencyId;

    @Column(name = "fte", columnDefinition = "VARCHAR(255)")
    private String fte;

    @Column(name = "pay_grade", columnDefinition = "VARCHAR(255)")
    private String payGrade;

    @Column(name = "pay_range_min", precision = 10, scale = 2)
    private BigDecimal payRangeMin;

    @Column(name = "pay_range_mid", precision = 10, scale = 2)
    private BigDecimal payRangeMid;

    @Column(name = "pay_range_max", precision = 10, scale = 2)
    private BigDecimal payRangeMax;

    @Column(name = "approved_budget", precision = 10, scale = 2)
    private BigDecimal approvedBudget;

    @Column(name = "recruiter", columnDefinition = "VARCHAR(255)")
    private String recruiterName;

    @Column(name = "hiring_manager", columnDefinition = "VARCHAR(255)")
    private String hiringManager;

    @Column(name = "head_of_business_unit", columnDefinition = "VARCHAR(255)")
    private String headOfBusinessUnit;

    @Column(name = "head_of_recruitment", columnDefinition = "VARCHAR(255)")
    private String headOfRecruitment;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "interviewing_competencies", columnDefinition = "TEXT")
    private String interviewingCompetencies;

    @Column(name = "referral_bonus", precision = 10, scale = 2)
    private BigDecimal referralBonus;

    @Column(name = "job_posting_start_date")
    private LocalDate jobPostingStartDate;

    @Column(name = "job_posting_end_date")
    private LocalDate jobPostingEndDate;

    @Column(name = "job_posting_type", columnDefinition = "VARCHAR(255)")
    private String jobPostingType;

    @Column(name = "job_posting_board", columnDefinition = "VARCHAR(255)")
    private String jobPostingBoard;

    public Requisition(BigInteger id){
        this.id = id;
    }
}
