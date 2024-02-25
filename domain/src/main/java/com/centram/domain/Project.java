package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.StringCommaSeparatedToListConverter;
import com.centram.domain.enumarator.*;
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
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "project", indexes = {@Index(name = "prjct_name_indx", columnList = "name", unique = false), @Index(name = "prjct_code_indx", columnList = "code", unique = true), @Index(name = "prjct_org_id_indx", columnList = "organisation_id", unique = false),})
@Audited
public class Project extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575312184473432054L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @NotNull
    @Column(name = "technology")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Technology technology;

    @NotNull
    @Valid
    @Column(name = "module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String moduleName;

    @NotNull
    @Valid
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String subModuleName;

    @NotNull
    @Valid
    @Column(name = "project_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private ProjectType projectType;

    @NotNull
    @Valid
    @Column(name = "project_billing_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private ProjectBillingType projectBillingType;

    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;


    @NotNull
    @Column(name = "code", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String code;

    @NotNull
    @Valid
    @Column(name = "uat_start", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime start;

    @NotNull
    @Valid
    @Column(name = "uat_end", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime end;

    @Valid
    @Lob
    @Column(name = "watch_list", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = StringCommaSeparatedToListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<String> watchList;

    @Valid
    @Lob
    @Column(name = "stake_holders", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = StringCommaSeparatedToListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<String> stakeHolders;

    @Valid
    @Lob
    @Column(name = "consultants", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = StringCommaSeparatedToListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<String> consultants;

    @NotNull
    @Valid
    @Column(name = "in_house")
    @JsonView(Views.BasicView.class)
    private Boolean inHouse;

    @NotNull
    @Valid
    @Column(name = "project_for")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private LicenseType projectFor;


    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;


    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    @Transient
    @JsonView(Views.BasicView.class)
    private String label;

    public Project(@NotNull BigInteger id) {
        this.id = id;
    }

    public Project(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }

    public String getLabel() {
        return this.getName().concat(" [").concat(this.getCode()).concat("]");
    }
}