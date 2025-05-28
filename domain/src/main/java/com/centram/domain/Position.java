package com.centram.domain;


import com.centram.common.view.Views;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(
        name = "position",
        uniqueConstraints = @UniqueConstraint(name = "position_constraint", columnNames = {"name", "department_id"}),
        indexes = {
                @Index(name = "position_department_idx", columnList = "department_id", unique = false),
        }
)
public class Position extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "fte")
    private BigDecimal fte;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @JsonIgnore
    private Department department;

    @Column(name = "location")
    private Long locationId;

    @Column(name = "cost_center")
    private String costCenter;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "pay_grad")
    private String payGrad;

    @Column(name = "standard_hour")
    private Integer standardHour;

    @Column(name = "to_be_hired")
    private Boolean toBeHired;

    @Column(name = "min_pay")
    private BigDecimal minPay;

    @Column(name = "mid_pay")
    private BigDecimal midPay;

    @Column(name = "max_pay")
    private BigDecimal maxPay;

    // view data
    @Transient
    @JsonView(Views.BasicView.class)
    private BigInteger departmentId;

    @Transient
    @JsonView(Views.BasicView.class)
    private BigInteger organisationId;

    @Transient
    @JsonView(Views.BasicView.class)
    private BigInteger divisionId;

    @Transient
    @JsonView(Views.BasicView.class)
    private BigInteger businessUnitId;
}
