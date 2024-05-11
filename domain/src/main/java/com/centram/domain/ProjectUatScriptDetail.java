package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.UATRemarkConverter;
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
import java.util.LinkedHashSet;
import java.util.List;

/**
 * ProjectUatDetail
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project_uat_script_detail", indexes = {@Index(name = "project_uat_script_detail_project_uat_script_id_index", columnList = "project_uat_script_id", unique = false),})
@Audited
public class ProjectUatScriptDetail extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -952600023593118974L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @NotNull
    @Valid
    @Column(name = "step", nullable = false)
    @JsonView(Views.BasicView.class)
    private Double step;

    @NotNull
    @Valid
    @Lob
    @Column(name = "action", nullable = false, columnDefinition = "TEXT not null")
    @JsonView(Views.BasicView.class)
    private String action;

    @NotNull
    @Valid
    @Lob
    @Column(name = "expected_result", nullable = false, columnDefinition = "TEXT not null")
    @JsonView(Views.BasicView.class)
    private String expectedResult;

    @Valid
    @Column(name = "actual_result")
    @JsonView(Views.BasicView.class)
    private String actualResult;

    @Valid
    @Column(name = "pass", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean pass;

    @Valid
    @Column(name = "retest_date", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDate retestDate;

    @Valid
    @Column(name = "retest_pass", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean retestPass;

    @Valid
    @Lob
    @Column(name = "remarks", nullable = true, columnDefinition = "TEXT default null")
    @JsonView(Views.BasicView.class)
    @Convert(converter = UATRemarkConverter.class)
    private LinkedHashSet<UATRemark> remarks;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "customer_user_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User customerUser;

    @Transient
    @JsonView(Views.BasicView.class)
    private Boolean previousStepPassed = false;

    @Transient
    @JsonView(Views.BasicView.class)
    private Boolean saved = false;

    @Valid
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    @Transient
    private List<MediaFile> attachments;

    public ProjectUatScriptDetail(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectUatScriptDetail(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}