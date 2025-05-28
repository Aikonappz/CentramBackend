package com.centram.domain;

import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(
        name = "division",
        uniqueConstraints = @UniqueConstraint(name = "division_constraint", columnNames = {"name", "business_unit_id"}),
        indexes = {
                @Index(name = "division_business_unit_idx", columnList = "business_unit_id", unique = false),
        }
)
public class Division extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "code")
    private String code;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = "division_head")
    private String divisionHead;

    @Column(name = "cost_center")
    private String costCenter;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "business_unit_id", referencedColumnName = "id")
    private BusinessUnit businessUnit;


    @OneToMany(mappedBy = "division", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments;
}
