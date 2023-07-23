package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

/**
 * Vendor
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project_uat", indexes = {@Index(name = "prjct_uat_org_id_indx", columnList = "organisation_id", unique = false), @Index(name = "prjct_uat_prj_id_indx", columnList = "project_id", unique = false),})
@Audited
public class ProjectUat extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -145029780828318960L;
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Project project;

    @NotNull
    @Valid
    @Column(name = "test_script_name")
    @JsonView(Views.BasicView.class)
    private String testScriptName;

    @NotNull
    @Valid
    @Lob
    @Column(name = "test_scenario", columnDefinition = "TEXT default null")
    @JsonView(Views.BasicView.class)
    private String testScenario;

    @Valid
    @Column(name = "test_scenario_job_id")
    @JsonView(Views.BasicView.class)
    private String testScenarioJobId;

    @Column(name = "planned_date", nullable = true, updatable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate plannedDate;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @Valid
    //@NotNull
    @OneToMany(mappedBy = "projectUat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonView(Views.BasicView.class)
    private Set<ProjectUatDetail> uatDetails;

    public ProjectUat(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectUat(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}