package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.ContactPersonConverter;
import com.centram.domain.converter.SettingConverter;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "Organisation")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "organisation"
        //indexes = {
        //        @Index(name = "org_name_idx", columnList = "name", unique = false)
        //}
)
@Audited
public class Organisation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.DetailView.class)
    private BigInteger id;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "add1", nullable = false, columnDefinition = "varchar(255) not null")
    private String add1;

    @ApiModelProperty(required = false, value = "")
    @Column(name = "add2", nullable = true, columnDefinition = "varchar(255) default null")
    private String add2;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "city", nullable = false, columnDefinition = "varchar(255) not null")
    private String city;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "pincode", nullable = false, columnDefinition = "varchar(255) not null")
    private String pincode;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "pan", nullable = false, columnDefinition = "varchar(255) not null")
    private String pan;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "tan", nullable = false, columnDefinition = "varchar(255) not null")
    private String tan;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Column(name = "gstin", nullable = false, columnDefinition = "varchar(255) not null")
    private String gstin;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "license_Type")
    @Enumerated(EnumType.ORDINAL)
    private LicenseType licenseType;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "license_start", nullable = false)
    private LocalDateTime licenseStart;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    @Column(name = "license_end", nullable = false)
    private LocalDateTime licenseEnd;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.DetailView.class)
    private Status status;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @Lob
    @Column(name = "setting", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = SettingConverter.class)
    private Setting setting;

    @ApiModelProperty(required = false, value = "")
    @Valid
    @Lob
    @Column(name = "contact_Persons", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = ContactPersonConverter.class)
    private List<ContactPerson> contactPersons;

    public Organisation(@NotNull BigInteger id) {
        this.id = id;
    }

    public Organisation(@NotNull BigInteger id, @NotNull Long version) {
        super(version);
        this.id = id;
    }
}