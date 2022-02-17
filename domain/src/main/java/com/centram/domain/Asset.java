package com.centram.domain;

import com.centram.common.view.Views;
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
                @UniqueConstraint(name = "inc_no_usr_constraint", columnNames = {"incident_no", "assigned_user_id"})
        },
        indexes = {
                @Index(name = "incident_no_indx", columnList = "incident_no", unique = false)
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

    @ApiModelProperty(required = false, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "asset_model_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private AssetModel assetModel;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "serial_number", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String serialNumber;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "asset_number", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String assetNo;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "is_department", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isDepartment = false;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "location_id", nullable = true, updatable = false, insertable = false, referencedColumnName = "id")
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
    @JoinColumn(name = "raised_for_location_id", nullable = true, updatable = false, insertable = false, referencedColumnName = "id")
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
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "approver_user1_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User approverUser1;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "approved_user1", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approvedUser1 = false;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "approver_user1_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String approverUser1Comment;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "approver_user2_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User approverUser2;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "approved_user2", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approvedUser2 = false;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "approver_user2_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String approverUser2Comment;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "approved_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime approvedAt;

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