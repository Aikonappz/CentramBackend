package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.common.vo.DepartmentVO;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;


import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Department
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "department",
        uniqueConstraints = @UniqueConstraint(name = "department_org_constraint", columnNames = {"name", "organisation_id"}),
        indexes = {
                @Index(name = "dept_org_idx", columnList = "organisation_id", unique = false),
        }
)
public class Department extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7161526376698505219L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;

    
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;

    public Department(@NotNull BigInteger id) {
        this.id = id;
    }

    public Department(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }

}