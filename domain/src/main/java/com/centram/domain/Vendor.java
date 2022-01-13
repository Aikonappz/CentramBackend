package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.IncidentAllocationType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Vendor")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
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

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String name;

    @ApiModelProperty(required = true, value = "")
    @Valid
    //@NotNull
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "vendor", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({Views.DetailView.class, Views.InternalView.class,})
    private List<VendorModule> vendorModules;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private Status status;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "ticket_allocation_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private IncidentAllocationType ticketAllocationType;

    @ApiModelProperty(required = true, value = "")
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