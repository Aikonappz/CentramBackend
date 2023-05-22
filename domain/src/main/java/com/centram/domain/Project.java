package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.StringCommaSeparetedToListConverter;
import com.centram.domain.enumarator.IncidentAllocationType;
import com.centram.domain.enumarator.Status;
import com.centram.domain.enumarator.ProjectType;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
import java.util.List;

/**
 * Vendor
 */
@ApiModel(description = "Project")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project",
        indexes = {
                @Index(name = "prjct_name_indx", columnList = "name", unique = false),
                @Index(name = "prjct_code_indx", columnList = "code", unique = true),
                @Index(name = "prjct_org_id_indx", columnList = "organisation_id", unique = false),
        }
)
@Audited
public class Project extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575312184473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "project_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private ProjectType projectType;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "code", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String code;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @Lob
    @Column(name = "watch_list", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = StringCommaSeparetedToListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<String> watchList;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "in_house")
    @JsonView(Views.BasicView.class)
    private Boolean inHouse;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    public Project(@NotNull BigInteger id) {
        this.id = id;
    }

    public Project(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}