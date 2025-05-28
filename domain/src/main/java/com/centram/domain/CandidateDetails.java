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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "candidate_details")
public class CandidateDetails extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "first_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String lastName;

    @Column(name = "middle_name", columnDefinition = "VARCHAR(255)")
    private String middleName;

    @Column(name = "email_address", columnDefinition = "VARCHAR(255) NOT NULL", unique = true)
    private String emailAddress;

    @Column(name = "phone_number", columnDefinition = "VARCHAR(20)")
    private String phoneNumber;

    @Column(name = "country_nationality", columnDefinition = "VARCHAR(255) NOT NULL")
    private String countryNationality;

    @Column(name = "resume_cv", columnDefinition = "TEXT")
    private String resumeCv;

    @Column(name = "payslips", columnDefinition = "TEXT")
    private String payslips;

    @Column(name = "national_id", columnDefinition = "VARCHAR(255)", unique = true)
    private String nationalId;

    @Column(name = "education_details", columnDefinition = "TEXT")
    private String educationDetails;

    @Column(name = "experience_details", columnDefinition = "TEXT")
    private String experienceDetails;

    @Column(name = "certificates", columnDefinition = "TEXT")
    private String certificates;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "experience_category", columnDefinition = "VARCHAR(255)")
    private String experienceCategory;

    @Column(name = "current_salary", precision = 10, scale = 2)
    private BigDecimal currentSalary;

    @Column(name = "expected_salary", precision = 10, scale = 2)
    private BigDecimal expectedSalary;

    @ManyToOne
    @JoinColumn(name = "requisition_id", referencedColumnName = "id", nullable = false)
    private Requisition requisition;
}
