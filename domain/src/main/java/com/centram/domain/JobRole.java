package com.centram.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_role")
public class JobRole extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 8126680899923310811L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "job_role_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String jobRoleName;

    @ManyToOne
    @JoinColumn(name = "family_id", nullable = false)
    private JobFamily jobFamily;

    @Column(name = "job_code_id", columnDefinition = "VARCHAR(255) NOT NULL")
    private String jobCodeId;

    @OneToMany(
            mappedBy = "jobRole",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Competency> competencies = new ArrayList<>();

    @OneToOne(
            mappedBy = "jobRole",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private JobProfile jobProfile;
}

