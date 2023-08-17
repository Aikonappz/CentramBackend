package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.StringCommaSeparatedToListConverter;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * Modules
 */

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "module", uniqueConstraints = {@UniqueConstraint(name = "mod_submod_constraint", columnNames = {"name", "parent_module_id"})})
public class Module implements Serializable {

    private static final long serialVersionUID = -4024535323361400625L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    private String name;

    @Column(name = "parent_module_id", nullable = true, columnDefinition = "BIGINT default null")
    private BigInteger parentModuleId;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @NotNull
    @Column(name = "projectModule")
    private Boolean projectModule;

    @Column(name = "app_module")
    private Boolean appModule;

    @Column(name = "app_module_description")
    private String appModuleDescription;

    @Column(name = "app_module_path")
    private String appModulePath;

    @Column(name = "app_module_icon")
    private String appModuleIcon;

    @Column(name = "app_feature_module")
    private Boolean appFeatureModule;

    @NotNull
    @Column(name = "licence_type")
    @Enumerated(EnumType.ORDINAL)
    private LicenseType licenseType;

    @Column(name = "customer_module_name")
    private String customerModuleName;

    @Column(name = "asset_ops_name")
    private String assetOPSName;

    @NotNull
    @Column(name = "ticketable")
    private Boolean ticketable;

    @NotNull
    @Column(name = "generate_asset_no")
    private Boolean generateAssetNo;

    @NotNull
    @Column(name = "require_approval")
    private Boolean requireApproval;

    @Valid
    @NotNull
    @Lob
    @Column(name = "models", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = StringCommaSeparatedToListConverter.class)
    @JsonView(Views.BasicView.class)
    private List<BigInteger> models;

    @Column(name = "dashboard_box_colour")
    private String dashboardBoxColour;

    @Valid
    @OneToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "organisation_id", nullable = true, referencedColumnName = "id")
    @JsonView(Views.BasicView.class)
    private Organisation organisation;

    @Transient
    private String parentModuleName;

    public Module(BigInteger id) {
        this.id = id;
    }
}