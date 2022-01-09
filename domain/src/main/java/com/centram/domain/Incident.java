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
@Table(name = "incident",
        uniqueConstraints = {
                @UniqueConstraint(name = "inc_no_usr_constraint", columnNames = {"incident_no", "assigned_user_id"})
        },
        indexes = {
                @Index(name = "incident_no_indx", columnList = "incident_no", unique = false)
        }
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
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String moduleName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String subModuleName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "title", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String title;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "incident_no", nullable = false, updatable = false, insertable = true, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String incidentNo;

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
    @OneToMany(mappedBy = "incident", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private Set<IncidentCommunication> communications;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "raised_at", nullable = false, updatable = false)
    @JsonView(Views.BasicView.class)
    private LocalDateTime raisedAt;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "sla_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime slaAt;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "hold_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime holdAt;

    public Incident(@NotNull BigInteger id) {
        this.id = id;
    }

    public Incident(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}