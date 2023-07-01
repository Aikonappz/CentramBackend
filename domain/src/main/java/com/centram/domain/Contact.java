package com.centram.domain;

import com.centram.domain.enumarator.ContactType;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Contact implements Serializable {
    private static final long serialVersionUID = 4277279142878797243L;
    private ContactType contactType;
    private String value;
    private Boolean primaryContact;
}
