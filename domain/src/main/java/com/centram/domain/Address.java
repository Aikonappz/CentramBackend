package com.centram.domain;

import com.centram.domain.enumarator.AddressType;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address implements Serializable {
    private static final long serialVersionUID = 2149040960400918629L;
    private AddressType addressType;
    private String add1;
    private String add2;
    private String zipcode;
    private String city;
    private String state;
    private String country;
    private Float lat;
    private Float lng;
}
