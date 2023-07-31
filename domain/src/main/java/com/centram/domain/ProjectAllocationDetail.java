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
import java.time.LocalDateTime;

/**
 * Vendor
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "project_allocation_detail", indexes = {@Index(name = "prjct_indx", columnList = "project_id", unique = false), @Index(name = "usr_indx", columnList = "user_id", unique = false)})
@Audited
public class ProjectAllocationDetail extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 4463732397819338770L;


    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;


    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "project_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Project project;


    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private User user;


    @NotNull
    @Column(name = "max_allocation", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String maxAllocation;

    @Column(name = "start_date", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime endDate;

    @Column(name = "deallocated", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean deallocated = false;

    public ProjectAllocationDetail(@NotNull BigInteger id) {
        this.id = id;
    }

    public ProjectAllocationDetail(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}