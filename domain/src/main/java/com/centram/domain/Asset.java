package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.AssetType;
import com.centram.domain.enumarator.ProductCategory;
import com.centram.domain.enumarator.PurchaseType;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Asset
 */
@ApiModel(description = "Asset")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "asset",
        uniqueConstraints = {
                @UniqueConstraint(name = "serial_no_org_constraint", columnNames = {"serial_no", "organisation_id"})
        },
        indexes = {
                @Index(name = "serial_no_indx", columnList = "serial_no", unique = false)
        }
)
@Audited
public class Asset extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575391234473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "product_category")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private ProductCategory productCategory;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "asset_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private AssetType assetType;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "model_no", columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String modelNo;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "serial_no", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String serialNo;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "is_department", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isDepartment = false;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "location_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Location location;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "department_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Department department;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_for_location_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Location raisedForLocation;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "is_under_warranty", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isUnderWarranty = false;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "warranty_expired_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime warrantyExpiredAt;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "purchase_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private PurchaseType purchaseType;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "rental_start_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime rentalStartAt;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "rental_end_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime rentalEndAt;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "is_available", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isAvailable = false;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "comment", nullable = true, columnDefinition = "varchar(2000) not null")
    @JsonView(Views.BasicView.class)
    private String comment;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "vendor_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Vendor vendor;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User raisedUser;

    public Asset(@NotNull BigInteger id) {
        this.id = id;
    }

    public Asset(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}