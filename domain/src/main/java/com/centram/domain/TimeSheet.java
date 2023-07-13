package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.composite.key.TimeSheetId;
import com.centram.domain.converter.TimeSheetEntriesConverter;
import com.centram.domain.enumarator.BillingType;
import com.centram.domain.enumarator.TaskType;
import com.centram.domain.enumarator.TimeSheetLocationType;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "time_sheet", indexes = {@Index(name = "ref_indx", columnList = "reference_id", unique = true), @Index(name = "usr_indx", columnList = "user_id", unique = false), @Index(name = "prjct_indx", columnList = "project_id", unique = false), @Index(name = "appr_indx", columnList = "approver_id", unique = false)})
@Audited
@IdClass(TimeSheetId.class)
public class TimeSheet extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7513901406995966145L;

    @Id
    @NotNull
    @Valid
    @Column(name = "week_start", nullable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate weekStart;


    @Id
    @NotNull
    @Valid
    @Column(name = "week_end", nullable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate weekEnd;

    @Id
    @NotNull
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Project project;

    @Id
    @NotNull
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User user;

    @Column(name = "reference_id")
    @JsonView(Views.BasicView.class)
    private UUID referenceId = UUID.randomUUID();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "task", nullable = false)
    @JsonView(Views.BasicView.class)
    private TaskType task;

    @NotNull
    @Valid
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "location_type", nullable = false)
    @JsonView(Views.BasicView.class)
    private TimeSheetLocationType location;

    @NotNull
    @Valid
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "billing_type", nullable = false)
    @JsonView(Views.BasicView.class)
    private BillingType billingType;

    @NotNull
    @Valid
    @Lob
    @Column(name = "time_sheet_entries", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = TimeSheetEntriesConverter.class)
    @JsonView(Views.BasicView.class)
    private Map<String, Float> timeSheetEntries;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "approver_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User approver;

    @Column(name = "approver_took_action", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approverTookAction = false;

    @Column(name = "approver_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String approverComment;

    @Column(name = "approved", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approved = false;

    public TimeSheet(@NotNull TimeSheetId timeSheetId) {

    }

    public TimeSheet(Long version, TimeSheetId timeSheetId) {
        super(version);

    }
}