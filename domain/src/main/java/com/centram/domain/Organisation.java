package com.centram.domain;

import com.centram.common.view.Views;
import com.centram.domain.converter.ContactPersonConverter;
import com.centram.domain.converter.SettingConverter;
import com.centram.domain.enumarator.CommunicationType;
import com.centram.domain.enumarator.LicenseType;
import com.centram.domain.enumarator.Status;
import com.fasterxml.jackson.annotation.JsonView;
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


@Validated

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

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    @JsonView(Views.DetailView.class)
    private BigInteger id;

    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) not null")
    private String name;

    @NotNull
    @Column(name = "add1", nullable = false, columnDefinition = "varchar(255) not null")
    private String add1;

    @Column(name = "add2", nullable = true, columnDefinition = "varchar(255) default null")
    private String add2;

    @NotNull
    @Column(name = "city", nullable = false, columnDefinition = "varchar(255) not null")
    private String city;

    @NotNull
    @Column(name = "pincode", nullable = false, columnDefinition = "varchar(255) not null")
    private String pincode;

    @NotNull
    @Column(name = "pan", nullable = false, columnDefinition = "varchar(255) not null")
    private String pan;

    @NotNull
    @Column(name = "tan", nullable = false, columnDefinition = "varchar(255) not null")
    private String tan;

    @NotNull
    @Column(name = "gstin", nullable = false, columnDefinition = "varchar(255) not null")
    private String gstin;

    @NotNull
    @Valid
    @Column(name = "license_Type")
    @Enumerated(EnumType.ORDINAL)
    private LicenseType licenseType;

    @NotNull
    @Valid
    @Column(name = "license_start", nullable = false)
    private LocalDateTime licenseStart;

    @NotNull
    @Valid
    @Column(name = "license_end", nullable = false)
    private LocalDateTime licenseEnd;

    @NotNull
    @Valid
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonView(Views.DetailView.class)
    private Status status;

    @Valid
    @Lob
    @Column(name = "setting", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = SettingConverter.class)
    private Setting setting;

    @Valid
    @Lob
    @Column(name = "contact_Persons", nullable = false, columnDefinition = "TEXT not null")
    @Convert(converter = ContactPersonConverter.class)
    private List<ContactPerson> contactPersons;

    @NotNull
    @Valid
    @Column(name = "centram_key", nullable = true)
    private String centramKey;

    @NotNull
    @Valid
    @Column(name = "centram_pass", nullable = true)
    private String centramPass;

    @NotNull
    @Valid
    @Column(name = "communication_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private CommunicationType communicationType;

    @NotNull
    @Valid
    @Column(name = "thired_party_key", nullable = true)
    private String thiredPartyKey;

    @NotNull
    @Valid
    @Column(name = "thired_party_pass", nullable = true)
    private String thiredPartyPass;

    public Organisation(@NotNull BigInteger id) {
        this.id = id;
    }

    public Organisation(@NotNull BigInteger id, @NotNull Long version) {
        super(version);
        this.id = id;
    }
}