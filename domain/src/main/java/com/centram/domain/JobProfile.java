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
@Table(name = "job_profile",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "role_id")
        }
)
public class JobProfile extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2667845427068799174L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @OneToOne
    @JoinColumn(name = "role_id", nullable = false, unique = true)
    private JobRole jobRole;

    @ManyToMany
    @JoinTable(
            name = "job_profile_competency",
            joinColumns = @JoinColumn(name = "job_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "competency_id")
    )
    private List<Competency> competencies = new ArrayList<>();

    @Lob
    @Column(name = "roles_and_responsibilities", columnDefinition = "TEXT")
    private String rolesAndResponsibilities;

    @Lob
    @Column(name = "education_background", columnDefinition = "TEXT")
    private String educationBackground;

    @Lob
    @Column(name = "experience_requirements", columnDefinition = "TEXT")
    private String experienceRequirements;

    @Lob
    @Column(name = "job_purpose", columnDefinition = "TEXT")
    private String jobPurpose;

    @Lob
    @Column(name = "key_roles_and_responsibilities_1", columnDefinition = "TEXT")
    private String keyRolesAndResponsibilities1;

    @Lob
    @Column(name = "key_roles_and_responsibilities_2", columnDefinition = "TEXT")
    private String keyRolesAndResponsibilities2;

    @Lob
    @Column(name = "key_roles_and_responsibilities_3", columnDefinition = "TEXT")
    private String keyRolesAndResponsibilities3;
}

