package com.centram.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "job_application")
public class JobApplication extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7849918059593252571L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "interview_status", columnDefinition = "VARCHAR(255) NOT NULL")
    private String interviewStatus;

    @Column(name = "interview_score", columnDefinition = "BIGINT NOT NULL")
    private BigInteger interviewScore;

    @Column(name = "interview_feedback_comments", columnDefinition = "TEXT")
    private String interviewFeedbackComments;

    @Column(name = "background_check_status", columnDefinition = "VARCHAR(255) NOT NULL")
    private String backgroundCheckStatus;

    @Column(name = "candidate_experience_categorization", columnDefinition = "VARCHAR(255) NOT NULL")
    private String candidateExperienceCategorization;

    @ManyToOne
    @JoinColumn(name = "candidate_details_id", referencedColumnName = "id", nullable = false)
    private CandidateDetails candidateDetails;
}

