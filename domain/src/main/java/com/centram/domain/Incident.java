package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.common.vo.CategoryAdminDashboardVO;
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
@NamedNativeQuery(name = "Incident.agingWiseIncidentDashboardData",
        query = " SELECT " +
                " sum(case when date_differnce >= 5 && date_differnce < 10 then 1 else 0 end) as aging5, " +
                " sum(case when date_differnce >= 10 && date_differnce < 20 then 1 else 0 end) as aging10, " +
                " sum(case when date_differnce >= 20 && date_differnce < 30 then 1 else 0 end) as aging20, " +
                " sum(case when date_differnce >= 30 && date_differnce < 60 then 1 else 0 end) as aging30, " +
                " sum(case when date_differnce > 60 then 1 else 0 end) as aging60 " +
                " from " +
                " ( " +
                "  select datediff(SYSDATE() ,i.created_date) as date_differnce from  incident i " +
                "  join user u on (u.id = i.raised_user_id) " +
                "  where u.organisation_id = (:organisationId) and " +
                "  ( " +
                "    ((:roleFilter) = true and i.module_id in (:userModules) and i.sub_module_id in (:userSubModules)) " +
                "    OR " +
                "    ((:roleFilter) = false) " +
                "  ) " +
                " and i.created_date BETWEEN (:start) and (:end) " +
                " ) tab ",
        resultSetMapping = "Mapping.CategoryAdminDashboardVO")
@SqlResultSetMapping(name = "Mapping.CategoryAdminDashboardVO",
        classes = @ConstructorResult(
                targetClass = CategoryAdminDashboardVO.class,
                columns = {
                        @ColumnResult(name = "aging5", type = Integer.class),
                        @ColumnResult(name = "aging10", type = Integer.class),
                        @ColumnResult(name = "aging20", type = Integer.class),
                        @ColumnResult(name = "aging30", type = Integer.class),
                        @ColumnResult(name = "aging60", type = Integer.class)
                }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "incident",
        uniqueConstraints = {
                @UniqueConstraint(name = "inc_no_usr_constraint", columnNames = {"incident_no", "organisation_id"})
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

    @Transient
    @JsonView(Views.BasicView.class)
    private String actualModuleName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String subModuleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String actualSubModuleName;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "title", nullable = false, columnDefinition = "varchar(255) not null" )
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

    @ApiModelProperty(required = false, value = "")
    @Column(name = "hold_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime holdAt;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "reopened_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime reopenedAt;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "agent_notification1_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime agentNotification1At;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "agent_notification2_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime agentNotification2At;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "escalation1_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime escalation1At;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "escalation2_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime escalation2At;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "sla_breached", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean slaBreached = false;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "re_opened", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean reOpened = false;

    @ApiModelProperty(required = false)
    @Column(name = "assignment_comment", nullable = true, columnDefinition = "varchar(1000) default null")
    @JsonView(Views.BasicView.class)
    private String assignmentComment;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    public Incident(@NotNull BigInteger id) {
        this.id = id;
    }

    public Incident(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}