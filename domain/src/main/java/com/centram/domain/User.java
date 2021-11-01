package com.centram.domain;

import com.centram.domain.converter.AddressConverter;
import com.centram.domain.converter.BankDetailConverter;
import com.centram.domain.converter.RoleConverter;
import com.centram.domain.enumarator.Status;
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

/**
 * User
 */
@ApiModel(description = "User")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "user",
        indexes = {
                @Index(name = "user_name_indx", columnList = "user_name", unique = true),
                @Index(name = "aadhar_idx", columnList = "aadhar", unique = false),
                @Index(name = "pan_idx", columnList = "pan", unique = false),
                @Index(name = "org_id_idx", columnList = "organisation_id", unique = false),
        }
)
@Audited
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2575337834473432054L;

    @ApiModelProperty(value = "")
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "first_name", columnDefinition = "varchar(255) not null")
    private String firstName;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "last_name", columnDefinition = "varchar(255) not null")
    private String lastName;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "user_name", columnDefinition = "varchar(255) not null")
    private String userName;

    @ApiModelProperty(value = "")
    @NotNull
    @Column(name = "password", columnDefinition = "varchar(255) not null")
    private String password;

    @ApiModelProperty(value = "")
    //@NotNull
    @Column(name = "aadhar")
    private String aadhar;

    @ApiModelProperty(value = "")
    //@NotNull
    @Column(name = "pan")
    private String pan;

    @ApiModelProperty(required = true, value = "")
    @Valid
    @NotNull
    @Lob
    @Column(name = "roles", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    private List<BigInteger> roles;

    @ApiModelProperty(value = "")
    @Valid
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "addresses", nullable = true)
    @Convert(converter = AddressConverter.class)
    private List<Address> addresses;

    @ApiModelProperty(value = "")
    @Valid
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

    @ApiModelProperty(required = true, value = "")
    @Valid
    @OneToOne
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;

    public User(@NotNull BigInteger id) {
        this.id = id;
    }

    public User(Long version, BigInteger id) {
        super(version);
        this.id = id;
    }
}