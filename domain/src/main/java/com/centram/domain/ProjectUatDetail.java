package com.centram.domain;

import com.centram.common.view.Views;
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
import java.time.LocalDate;

/**
 * ProjectUatDetail
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project_uat_detail", indexes = {@Index(name = "prjct_uat_id_indx", columnList = "project_uat_id", unique = false),})
@Audited
public class ProjectUatDetail extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -145029780828318960L;
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @NotNull
    @Valid
    @Column(name = "step", columnDefinition = "TEXT not null")
    @JsonView(Views.BasicView.class)
    private String step;

    @NotNull
    @Valid
    @Lob
    @Column(name = "action", columnDefinition = "TEXT not null")
    @JsonView(Views.BasicView.class)
    private String action;

    @NotNull
    @Valid
    @Lob
    @Column(name = "expected_result", columnDefinition = "TEXT not null")
    @JsonView(Views.BasicView.class)
    private String expectedResult;

    @NotNull
    @Valid
    @Column(name = "actual_result")
    @JsonView(Views.BasicView.class)
    private String actualResult;

    @NotNull
    @Valid
    @Column(name = "pass")
    @JsonView(Views.BasicView.class)
    private Boolean pass;

    @Valid
    @Column(name = "retest_date", nullable = true, updatable = false)
    @JsonView(Views.BasicView.class)
    private LocalDate retestDate;

    @Valid
    @Column(name = "pass")
    @JsonView(Views.BasicView.class)
    private Boolean retestPass;

    @Valid
    @Lob
    @Column(name = "remarks", columnDefinition = "TEXT default null")
    @JsonView(Views.BasicView.class)
    private String remarks;

    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_uat_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private ProjectUat projectUat;

    public ProjectUatDetail(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectUatDetail(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}