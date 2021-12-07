package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.WatchListConverter;
import com.centram.domain.enumarator.IncidentStatus;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Incident
 */
@ApiModel(description = "Incident")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "incident"
        /*,
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_org_constraint", columnNames = {"email", "organisation_id"}),
                @UniqueConstraint(name = "user_empid_org_constraint", columnNames = {"employee_id", "organisation_id"})
        },
        indexes = {
                @Index(name = "email_indx", columnList = "email", unique = true),
                @Index(name = "employeeId_indx", columnList = "employee_id", unique = false),
                @Index(name = "managerId_indx", columnList = "manager_id", unique = false),
                @Index(name = "employeeId_org_indx", columnList = "employee_id,organisation_id", unique = false),
                @Index(name = "managerId__org_indx", columnList = "manager_id,organisation_id", unique = false),
                @Index(name = "org_id_idx", columnList = "organisation_id", unique = false),
        }*/
)
@Audited
public class Incident extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "module_id", nullable = false)
    @JsonView({Views.DetailView.class, Views.InternalView.class})
    private BigInteger moduleId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView({Views.DetailView.class, Views.InternalView.class})
    private BigInteger subModuleId;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "title", nullable = false, columnDefinition = "varchar(1000) not null")
    @JsonView(Views.BasicView.class)
    private String title;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "priority_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Priority priority;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @Lob
    @Column(name = "watch_list", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = WatchListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<String> watchList;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private IncidentStatus status;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User raisedUser;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "assigned_user_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class,})
    private User assignedUser;

    @ApiModelProperty(required = false, value = "")
    @Valid
    //@NotNull
    @OneToMany(mappedBy = "incident", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private Set<IncidentCommunication> communications;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "raisedAt", nullable = false, updatable = false)
    @JsonView(Views.BasicView.class)
    private LocalDateTime raisedAt;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "slaAt", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime slaAt;

    public Incident(@NotNull BigInteger id) {
        this.id = id;
    }

    public Incident(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}