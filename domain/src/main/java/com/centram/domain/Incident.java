package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.common.vo.CategoryAdminDashboardVO;
import com.centram.domain.converter.StringCommaSeparetedToListConverter;
import com.centram.domain.converter.TimeEntryConverter;
import com.centram.domain.enumarator.IncidentStatus;
import com.centram.domain.enumarator.LicenseType;
import com.fasterxml.jackson.annotation.JsonView;


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

@Validated

@NamedNativeQuery(
        name = "Incident.agingWiseIncidentDashboardData",
        query = " SELECT " +
                " sum(case when date_differnce >= 5 && date_differnce < 10 then 1 else 0 end) as aging5, " +
                " sum(case when date_differnce >= 10 && date_differnce < 20 then 1 else 0 end) as aging10, " +
                " sum(case when date_differnce >= 20 && date_differnce < 30 then 1 else 0 end) as aging20, " +
                " sum(case when date_differnce >= 30 && date_differnce < 60 then 1 else 0 end) as aging30, " +
                " sum(case when date_differnce > 60 then 1 else 0 end) as aging60 " +
                " from " +
                " ( " +
                "  select datediff(SYSDATE() ,i.created_date) as date_differnce from incident i " +
                "  join user u on (u.id = i.raised_user_id) and i.incident_type = 1 " +
                "  where u.organisation_id = (:organisationId) and " +
                "  ( " +
                "    ((:roleFilter) = true and i.module_id in (:userModules) and i.sub_module_id in (:userSubModules)) " +
                "    OR " +
                "    ((:roleFilter) = false) " +
                "  ) " +
                " and i.created_date BETWEEN (:start) and (:end) " +
                " ) tab ",
        resultSetMapping = "Mapping.CategoryAdminDashboardVO"
)
@SqlResultSetMapping(
        name = "Mapping.CategoryAdminDashboardVO",
        classes = @ConstructorResult(
                targetClass = CategoryAdminDashboardVO.class,
                columns = {
                        @ColumnResult(name = "aging5", type = Integer.class),
                        @ColumnResult(name = "aging10", type = Integer.class),
                        @ColumnResult(name = "aging20", type = Integer.class),
                        @ColumnResult(name = "aging30", type = Integer.class),
                        @ColumnResult(name = "aging60", type = Integer.class)
                }
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "incident",
        uniqueConstraints = {
                @UniqueConstraint(name = "inc_no_org_id_unq_key", columnNames = {"incident_no", "organisation_id"})
        },
        indexes = {
                @Index(name = "inc_no_indx", columnList = "incident_no", unique = false),
                @Index(name = "org_id_indx", columnList = "organisation_id", unique = false)
        }
)
@Audited
public class Incident extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;


    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;


    @NotNull
    @Column(name = "module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;


    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String moduleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String actualModuleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String subModuleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String actualSubModuleName;


    @NotNull
    @Column(name = "title", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String title;


    @NotNull
    @Column(name = "incident_no", nullable = false, updatable = false, insertable = true, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String incidentNo;


    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "priority_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Priority priority;


    @Valid
    @Lob
    @Column(name = "watch_list", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = StringCommaSeparetedToListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<String> watchList;


    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private IncidentStatus status;


    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User raisedUser;


    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "assigned_user_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class,})
    private User assignedUser;


    @Valid
    //@NotNull
    @OneToMany(mappedBy = "incident", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private Set<IncidentCommunication> communications;


    @NotNull
    @Column(name = "raised_at", nullable = false, updatable = false)
    @JsonView(Views.BasicView.class)
    private LocalDateTime raisedAt;


    @Column(name = "sla_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime slaAt;


    @Column(name = "hold_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime holdAt;


    @Column(name = "reopened_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime reopenedAt;


    @Column(name = "agent_notification1_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime agentNotification1At;


    @Column(name = "agent_notification2_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime agentNotification2At;


    @Column(name = "escalation1_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime escalation1At;


    @Column(name = "escalation2_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime escalation2At;


    @Column(name = "sla_breached", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean slaBreached = false;


    @Column(name = "re_opened", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean reOpened = false;


    @Column(name = "assignment_comment", nullable = true, columnDefinition = "varchar(1000) default null")
    @JsonView(Views.BasicView.class)
    private String assignmentComment;


    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;


    @NotNull
    @Valid
    @Column(name = "incident_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private LicenseType incidentType;


    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "asset_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Asset asset;


    @Column(name = "old_asset_id", nullable = true)
    @JsonView(Views.BasicView.class)
    private BigInteger oldAssetId;


    @Column(name = "old_asset", nullable = true)
    @JsonView(Views.BasicView.class)
    private String oldAsset;


    @Column(name = "approval_required", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approvalRequired = false;


    @Valid
    @NotNull
    @JsonView({Views.BasicView.class, Views.ListView.class, Views.DetailView.class, Views.InternalView.class,})
    private BigInteger approverUserId;


    @Column(name = "asset_approved", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean assetApproved = false;


    @Column(name = "feedback_provided", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean feedbackProvided = false;


    @Column(name = "allocated", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean allocated = false;


    @Column(name = "allocation_date_time", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime allocationDateTime;


    @Column(name = "deallocated", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean deallocated = false;


    @Column(name = "dealocation_date_time", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime deallocationDateTime;


    @Column(name = "asset_ticket_type", nullable = true)
    @JsonView(Views.BasicView.class)
    private String ticketType;


    @Valid
    @Column(name = "asset_validity", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime assetValidity;


    @Column(name = "validity_expiration_msg_sent", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean validityExpirationMessageSent = false;


    @Column(name = "validity_expired_msg_sent", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean validityExpiredMessageSent = false;


    @NotNull
    @Column(name = "expected_time", nullable = true, columnDefinition = "varchar(5) default '00:00'")
    @JsonView(Views.BasicView.class)
    private String expectedTime;


    @Valid
    @Lob
    @Column(name = "time_entries", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = TimeEntryConverter.class)
    @JsonView(Views.BasicView.class)
    private List<TimeEntry> timeEntries;

    @Transient
    private com.centram.domain.Module category;

    @Transient
    private com.centram.domain.Module subCategory;

    public Incident(@NotNull BigInteger id) {
        this.id = id;
    }

    public Incident(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}