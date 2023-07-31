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
 * Asset order
 */

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "asset_order",
        uniqueConstraints = @UniqueConstraint(name = "ast_ord_no_org_constraint", columnNames = {"order_no", "organisation_id"}),
        indexes = {
                @Index(name = "ast_ord_org_indx", columnList = "organisation_id", unique = false)
        }
)
@Audited
public class AssetOrder extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575390834473432054L;

    
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    
    @Column(name = "order_no", updatable = false, insertable = true, nullable = false, columnDefinition = "varchar(255)")
    @JsonView(Views.BasicView.class)
    private String orderNo;

    
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

    
    @Column(name = "model", nullable = true)
    @JsonView(Views.BasicView.class)
    private String model;

    
    @Column(name = "quantity", nullable = true)
    @JsonView(Views.BasicView.class)
    private Integer quantity;

    
    @Column(name = "currency", nullable = true)
    @JsonView(Views.BasicView.class)
    private String currency;

    
    @Column(name = "within_budget", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean withinBudget = false;

    
    @Column(name = "limit_amount", nullable = true)
    @JsonView(Views.BasicView.class)
    private Double limitAmount;

    
    @Column(name = "extra_amount", nullable = true)
    @JsonView(Views.BasicView.class)
    private Double extraAmount;

    
    @Column(name = "total_amount", nullable = true)
    @JsonView(Views.BasicView.class)
    private Double totalAmount;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "approver_user1_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User approverUser1;

    
    @Column(name = "approved_user1", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approvedUser1 = false;

    
    @Column(name = "approver_user1_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String approverUser1Comment;

    @Column(name = "approver_user1_feedback_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime approverUser1FeedbackAt;

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "approver_user2_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User approverUser2;

    
    @Column(name = "approved_user2", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean approvedUser2 = false;

    
    @Column(name = "approver_user2_comment", nullable = true)
    @JsonView(Views.BasicView.class)
    private String approverUser2Comment;

    @Column(name = "approver_user2_feedback_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime approverUser2FeedbackAt;

    
    @Valid
    @OneToOne
    @JoinColumn(name = "vendor_id", nullable = true, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Vendor vendor;

    
    @Column(name = "existing_agreement", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean existingAgreement = false;

    @Column(name = "agreement_end_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime agreementEndAt;

    
    @NotNull
    @Valid
    @Column(name = "purchase_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private PurchaseType purchaseType;

    
    @Column(name = "rent_duration", nullable = true)
    @JsonView(Views.BasicView.class)
    private String rentDuration;

    /*@Column(name = "rent_start_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime rentStartAt;

    @Column(name = "rent_end_at", nullable = true)
    @JsonView(Views.BasicView.class)
    private LocalDateTime rentEndAt;*/

    
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User raisedUser;

    
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    
    @NotNull
    @Column(name = "other_details", columnDefinition = "varchar(255)")
    @JsonView(Views.BasicView.class)
    private String otherDetails;

    public AssetOrder(@NotNull BigInteger id) {
        this.id = id;
    }

    public AssetOrder(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}