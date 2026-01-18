package com.centram.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "requisition_recruiter_review")
public class RequisitionRecruiterReview extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 6622033593957596557L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "status", columnDefinition = "VARCHAR(255) NOT NULL")
    private String status;

    @OneToOne
    @JoinColumn(name = "requisition_id", referencedColumnName = "id")
    private Requisition requisition;

    @Column(name = "job_start_date")
    private LocalDate jobStartDate;

    @Column(name = "job_end_date")
    private LocalDate jobEndDate;

    @Column(name = "internal_job_title", columnDefinition = "VARCHAR(255)")
    private String internalJobTitle;

    @Column(name = "internal_job_description", columnDefinition = "TEXT")
    private String internalJobDescription;

    @Column(name = "external_job_title", columnDefinition = "VARCHAR(255)")
    private String externalJobTitle;

    @Column(name = "external_job_description", columnDefinition = "TEXT")
    private String externalJobDescription;

    @Column(name = "level_of_experience", columnDefinition = "VARCHAR(255)")
    private String levelOfExperience;

    @Column(name = "job_location", columnDefinition = "VARCHAR(255)")
    private String jobLocation;

    @Column(name = "position_id", columnDefinition = "BIGINT")
    private BigInteger positionId;

    @Column(name = "business_unit_id", columnDefinition = "BIGINT")
    private BigInteger businessUnitId;

    @Column(name = "division_id", columnDefinition = "BIGINT")
    private BigInteger divisionId;

    @Column(name = "department_id", columnDefinition = "BIGINT")
    private BigInteger departmentId;

    @Column(name = "currency_id", columnDefinition = "BIGINT")
    private BigInteger currencyId;

    @Column(name = "fte", columnDefinition = "VARCHAR(255)")
    private String fte;

    @Column(name = "salary_min", columnDefinition = "VARCHAR(255)")
    private String salaryMin;

    @Column(name = "salary_mid", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal salaryMid;

    @Column(name = "salary_max", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal salaryMax;

    @Column(name = "hiring_manager", columnDefinition = "VARCHAR(255)")
    private String hiringManager;

    @Column(name = "recruiter", columnDefinition = "VARCHAR(255)")
    private String recruiter;

    @Column(name = "recruiting_team_lead", columnDefinition = "VARCHAR(255)")
    private String recruitingTeamLead;

    @Column(name = "notification_status", columnDefinition = "VARCHAR(255) NOT NULL")
    private String notificationStatus;

}
