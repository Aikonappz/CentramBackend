package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.enumarator.AssetType;
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

/**
 * Asset order
 */
@ApiModel(description = "Asset order")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "asset_order",
        indexes = {
                @Index(name = "ast_ord_org_indx", columnList = "organisation_id", unique = false)
        }
)
@Audited
public class AssetOrder extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575390834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.BasicView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "order_no", updatable = false, insertable = true, nullable = false, columnDefinition = "varchar(255) default null")
    @JsonView(Views.BasicView.class)
    private String orderNo;

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

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "asset_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private AssetType assetType;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "quantity", nullable = true)
    @JsonView(Views.BasicView.class)
    private Integer quantity;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "cost", nullable = true)
    @JsonView(Views.BasicView.class)
    private Double cost;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "within_budget", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean withinBudget = false;

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

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "purchase_type")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.BasicView.class)
    private PurchaseType purchaseType;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "existing_agreement", nullable = true)
    @JsonView(Views.BasicView.class)
    private Boolean existingAgreement = false;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "vendor_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private Vendor vendor;

    @ApiModelProperty(required = true, value = "")
    @Column(name = "comment", nullable = true, columnDefinition = "varchar(2000) not null")
    @JsonView(Views.BasicView.class)
    private String comment;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(name = "raised_user_id", nullable = false, referencedColumnName = "id")
    @JsonView({Views.BasicView.class, Views.DetailView.class, Views.InternalView.class,})
    private User raisedUser;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", nullable = false, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    public AssetOrder(@NotNull BigInteger id) {
        this.id = id;
    }

    public AssetOrder(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}