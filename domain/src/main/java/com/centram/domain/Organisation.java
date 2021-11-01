package com.centram.domain;

import com.centram.domain.converter.BankDetailConverter;
import com.centram.domain.converter.SettingConverter;
import com.centram.domain.enumarator.Status;
import com.centram.domain.converter.AddressConverter;
import com.centram.domain.converter.ContactConverter;
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
@Table(name = "organisation",
        indexes = {
                @Index(name = "org_name_idx", columnList = "name", unique = false),
                @Index(name = "org_mnemonic_idx", columnList = "mnemonic", unique = true),
        }
)
@Audited
public class Organisation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "mnemonic", columnDefinition = "varchar(255) not null")
    private String mnemonic;

    @ApiModelProperty(value = "")
    @Valid
    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "addresses", nullable = true)
    @Convert(converter = AddressConverter.class)
    private List<Address> addresses;

    @ApiModelProperty(value = "")
    @Valid
    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "setting", nullable = true)
    @Convert(converter = SettingConverter.class)
    private Setting setting;


    @ApiModelProperty(value = "")
    @Valid
    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "contacts", nullable = true)
    @Convert(converter = ContactConverter.class)
    private List<Contact> contacts;

    @ApiModelProperty(value = "")
    @Valid
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "bank_details", nullable = true)
    @Convert(converter = BankDetailConverter.class)
    private List<BankDetail> bankDetails;

    @ApiModelProperty(value = "")
    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public Organisation(@NotNull BigInteger id) {
        this.id = id;
    }
}