package com.centram.domain;

import com.centram.common.view.Views;
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
@Table(name = "project_uat", indexes = {@Index(name = "project_uat_organisation_id_index", columnList = "organisation_id", unique = false), @Index(name = "project_uat_project_id_module_id_sub_module_id_index", columnList = "project_id,module_id,sub_module_id", unique = false),})
@Audited
public class ProjectUat extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3719954672398349583L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @Valid
    @NotNull
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Project project;

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

    public ProjectUat(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectUat(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}