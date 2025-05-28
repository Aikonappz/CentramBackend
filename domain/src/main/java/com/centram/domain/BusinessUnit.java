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
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "business_unit",
        uniqueConstraints = @UniqueConstraint(name = "business_unit_constraint", columnNames = {"name", "organisation_id"}),
        indexes = {
                @Index(name = "business_unit_organisation_id_idx", columnList = "organisation_id", unique = false),
        }
)
public class BusinessUnit extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "name", columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "code")
    private String code;

    @Column(name = "cost_center")
    private String costCenter;

    @Column(name = "bu_head")
    private String buHead;

    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    @JsonIgnore
    private Organisation organisation;

    @OneToMany(mappedBy = "businessUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Division> divisions;


}