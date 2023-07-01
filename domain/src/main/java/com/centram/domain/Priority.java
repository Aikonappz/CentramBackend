package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.PriorityType;
import com.centram.domain.enumarator.Status;
import com.centram.domain.enumarator.VendorType;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
@Table(
        name = "priority",
        uniqueConstraints = @UniqueConstraint(name = "priority_org_type_constraint", columnNames = {"name", "organisation_id","priority_type"}),
        indexes = {
                @Index(name = "prty_org_idx", columnList = "organisation_id", unique = false),
        }
)
public class Priority extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7161816376698505219L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private BigInteger id;

    
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private String name;

    
    @NotNull
    @Column(name = "description", columnDefinition = "varchar(2000) not null")
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private String description;

    
    @NotNull
    @Column(name = "sla", columnDefinition = "varchar(255) not null")
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private String sla;

    
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView({Views.BasicView.class, Views.DetailView.class})
    private Status status;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;

    
    @NotNull
    @Valid
    @Column(name = "priority_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private PriorityType priorityType;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}