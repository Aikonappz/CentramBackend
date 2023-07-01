package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.IncidentAllocationType;
import com.centram.domain.enumarator.Status;
import com.centram.domain.enumarator.VendorType;
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
@Table(name = "vendor",
        indexes = {
                @Index(name = "vndr_name_indx", columnList = "name", unique = false),
                @Index(name = "vndr_org_id_indx", columnList = "organisation_id", unique = false),
        }
)
@Audited
public class Vendor extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575312184473432054L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;

    
    @NotNull
    @Column(name = "contact_name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactName;

    
    @NotNull
    @Column(name = "contact_email", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactEmail;

    
    @NotNull
    @Column(name = "contact_number", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactNumber;

    
    @NotNull
    @Column(name = "contact_address", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String contactAddress;

    
    @Valid
    //@NotNull
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "vendor", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private List<VendorModule> vendorModules;

    
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    
    @NotNull
    @Valid
    @Column(name = "vendor_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private VendorType vendorType;

    
    @NotNull
    @Valid
    @Column(name = "ticket_allocation_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private IncidentAllocationType ticketAllocationType;

    
    @NotNull
    @Valid
    @Column(name = "in_house")
    @JsonView(Views.BasicView.class)
    private Boolean inHouse;

    public Vendor(@NotNull BigInteger id) {
        this.id = id;
    }

    public Vendor(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}