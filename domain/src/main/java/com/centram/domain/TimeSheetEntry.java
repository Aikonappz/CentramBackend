package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.TimeEntryConverter;
import com.centram.domain.converter.TimeSheetDateTimeEntryConverter;
import com.centram.domain.enumarator.BillingType;
import com.centram.domain.enumarator.TaskType;
import com.centram.domain.enumarator.TimeSheetLocationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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
@Table(name = "time_sheet_entry", indexes = {
        @Index(name = "time_sheet_entry_project_index", columnList = "project_id", unique = false),
        @Index(name = "time_sheet_entry_time_sheet_index", columnList = "time_sheet_id", unique = false),
        @Index(name = "time_sheet_entry_approver_index", columnList = "approver_id", unique = false),
})
@Audited
public class    TimeSheetEntry extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 3250683122623301346L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Project project;
    @Enumerated(EnumType.STRING)
    @Column(name = "task", nullable = false)
    @JsonView(Views.BasicView.class)
    private TaskType task;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "billing_type", nullable = false)
    @JsonView(Views.BasicView.class)
    private BillingType billingType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "location_type", nullable = false)
    @JsonView(Views.BasicView.class)
    private TimeSheetLocationType location;
    @Column(name = "user_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String userComment;
    @Column(name = "approver_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String approverComment;
    @Column(name = "approved", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approved = false;
    @Column(name = "rejected", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean rejected = false;
    @Valid
    @Lob
    @Column(name = "time_entries", nullable = true, columnDefinition = "TEXT")
    @Convert(converter = TimeSheetDateTimeEntryConverter.class)
    @JsonView(Views.BasicView.class)
    private Map<LocalDate, LocalTime> timeEntries;
    @JsonManagedReference
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_sheet_id", nullable = true, referencedColumnName = "id")
    private TimeSheet timeSheet;
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "approver_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User approver;
}