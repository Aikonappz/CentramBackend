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
@Table(name = "job_family")
public class JobFamily extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2412509893434606405L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "job_family_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String jobFamilyName;

    @OneToMany(
            mappedBy = "jobFamily",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<JobRole> jobRoles = new ArrayList<>();
}
