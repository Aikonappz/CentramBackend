package com.centram.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offer_details")
@Getter
@Setter
@NoArgsConstructor
public class OfferDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_documents")
    private String candidateDocumentsPath;

    @Column(name = "current_salary", precision = 10, scale = 2)
    private BigDecimal currentSalary;

    @Column(name = "proposed_salary", precision = 10, scale = 2)
    private BigDecimal proposedSalary;

    @Column(name = "proposed_grade")
    private String proposedGrade;

    @Column(name = "proposed_title")
    private String proposedTitle;

    @Column(name = "proposed_location")
    private String proposedLocation;

    @Column(name = "proposed_start_date")
    private LocalDate proposedStartDate;

    @Column(name = "proposed_contract_type")
    private String proposedContractType;

    @ManyToOne
    @JoinColumn(name = "candidate_details_id", referencedColumnName = "id", nullable = false)
    private JobApplication jobApplication;
}
