package com.centram.domain;


import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Country
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "country")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
public class Country extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7161526376698505219L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    @Column(name = "country_name", unique = true)
    private String countryName;

    @Column(name="isd_code")
    private String isdCode;

    @Column(name="iso_code")
    private String isoCode;

}