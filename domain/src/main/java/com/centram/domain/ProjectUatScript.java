package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
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
 * ProjectUatScript
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project_uat_script", indexes = {@Index(name = "project_uat_script_project_uat_id_index", columnList = "project_uat_id", unique = false),})
@Audited
public class ProjectUatScript extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4081362839129406536L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @NotNull
    @Valid
    @Column(name = "test_script_name", nullable = false)
    @JsonView(Views.BasicView.class)
    private String testScriptName;

    @NotNull
    @Valid
    @Lob
    @Column(name = "test_scenario", nullable = false, columnDefinition = "TEXT")
    @JsonView(Views.BasicView.class)
    private String testScenario;

    @Column(name = "planned_date", nullable = false, updatable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate plannedDate;

    @Valid
    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_uat_script_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Set<ProjectUatScriptDetail> projectUatScriptDetails;

    @Valid
    @NotNull
    @Column(name = "uat_complete", nullable = false)
    @JsonView(Views.BasicView.class)
    private Boolean uatComplete = false;

    public ProjectUatScript(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectUatScript(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}