package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.Technology;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ProjectUat
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project_uat", indexes = {@Index(name = "project_uat_organisation_id_index", columnList = "organisation_id", unique = false),})
@Audited
public class ProjectUat extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3719954672398349583L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @NotNull
    @Valid
    @Column(name = "technology", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Technology technology;

    @NotNull
    @Valid
    @Column(name = "module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    @NotNull
    @Valid
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @Valid
    @NotNull
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Project project;

    @NotNull
    @Valid
    @Column(name = "uat_cycle_name", nullable = false)
    @JsonView(Views.BasicView.class)
    private String uatCycleName;

    @Valid
    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_uat_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Set<ProjectUatScript> projectUatScripts;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User uploadedBy;

    @Valid
    @NotNull
    @Column(name = "uat_cycle_complete", nullable = false)
    @JsonView(Views.BasicView.class)
    private Boolean uatCycleComplete = false;

    @Transient
    @JsonView(Views.BasicView.class)
    private String moduleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String subModuleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private MediaFile uatScript;

    @Transient
    @JsonView(Views.BasicView.class)
    private MediaFile uatManual;

    @Transient
    @JsonView(Views.BasicView.class)
    private Boolean canMarkComplete = false;

    @Transient
    @JsonView(Views.BasicView.class)
    private String status;

    @Transient
    @JsonView(Views.BasicView.class)
    private Map<String,String> actionDetails = new HashMap<String,String>();

    public ProjectUat(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectUat(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}