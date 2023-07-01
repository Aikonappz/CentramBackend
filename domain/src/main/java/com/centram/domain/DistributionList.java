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
import java.util.List;
import java.util.Set;

/**
 * Incident
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "distribution_list",
        indexes = {
                @Index(name = "dl_dl_name_org_indx", columnList = "dl_name,organisation_id", unique = false),
                @Index(name = "dl_dl_name_indx", columnList = "dl_name", unique = false),
                @Index(name = "dl_org_id_indx", columnList = "organisation_id", unique = false),
        }
)
@Audited
public class DistributionList extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337184473432054L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "dl_name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String dlName;

    
    @NotNull
    @Column(name = "dl_email", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String dlEmail;

    
    @Valid
    //@NotNull
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "distributionList", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private List<DistributionListModule> distributionListModules;

    
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    private Organisation organisation;

    public DistributionList(@NotNull BigInteger id) {
        this.id = id;
    }

    public DistributionList(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}