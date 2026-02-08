package com.centram.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "job_portal_application",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_application",
                        columnNames = {"candidate_id", "job_posting_id"}
                )
        }
)
public class JobPortalApplication extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -9200994883150920000L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "candidate_id", nullable = false)
    private BigInteger candidateId;

    @Column(name = "job_posting_id", nullable = false)
    private BigInteger jobPostingId;

    @Column(nullable = false)
    private String applicationStatus; // APPLIED / SHORTLISTED / REJECTED
}
