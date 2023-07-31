package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.PurchaseType;
import com.fasterxml.jackson.annotation.JsonView;


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

@Validated

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

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @NotNull
    @Column(name = "module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger moduleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String moduleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String actualModuleName;

    
    @NotNull
    @Column(name = "sub_module_id", nullable = false)
    @JsonView(Views.BasicView.class)
    private BigInteger subModuleId;

    @Transient
    @JsonView(Views.BasicView.class)
    private String subModuleName;

    @Transient
    @JsonView(Views.BasicView.class)
    private String actualSubModuleName;

    
    @NotNull
    @Column(name = "model_no", columnDefinition = "varchar(255)")
    @JsonView(Views.BasicView.class)
    private String modelNo;

    
    @NotNull
    @Column(name = "serial_no", nullable = false, columnDefinition = "varchar(255) not null")
    @JsonView(Views.BasicView.class)
    private String serialNo;

    
    @Column(name = "is_department", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isDepartment = false;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "location_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Location location;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "department_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Department department;

    
    @Column(name = "is_location", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isLocation = false;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_for_location_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Location raisedForLocation;

    
    @Column(name = "is_under_warranty", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isUnderWarranty = false;

    
    @Column(name = "warranty_expired_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime warrantyExpiredAt;

    
    @NotNull
    @Valid
    @Column(name = "purchase_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private PurchaseType purchaseType;

    
    @Column(name = "rental_start_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime rentalStartAt;

    
    @Column(name = "rental_end_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime rentalEndAt;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "vendor_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Vendor vendor;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "order_requested_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User orderRequestedUser;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "approver1_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User approverUser1;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "approver2_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User approverUser2;

    
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    
    @Column(name = "is_available", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean isAvailable = false;

    
    @NotNull
    @Column(name = "other_details", columnDefinition = "varchar(255)")
    @JsonView(Views.BasicView.class)
    private String otherDetails;

    
    @Column(name = "warranty_expiration_msg_sent", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean warrantyExpirationMessageSent = false;

    
    @Column(name = "warranty_expired_msg_sent", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean warrantyExpiredMessageSent = false;

    public Asset(@NotNull BigInteger id) {
        this.id = id;
    }

    public Asset(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}