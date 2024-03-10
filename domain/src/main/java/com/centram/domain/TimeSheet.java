package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.composite.key.TimeSheetId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "time_sheet", indexes = {
        @Index(name = "usr_indx", columnList = "user_id", unique = false),
    }
)
@Audited
//@IdClass(TimeSheetId.class)
public class TimeSheet extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4973859325977150716L;
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;
    @Column(name = "start_date", nullable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate endDate;
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User user;
    @JsonBackReference
    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "timeSheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.BasicView.class)
    private List<TimeSheetEntry> timeSheetEntries;

    public TimeSheet(@NotNull TimeSheetId timeSheetId) {
    }

    public TimeSheet(Long version, TimeSheetId timeSheetId) {
        super(version);
    }
}