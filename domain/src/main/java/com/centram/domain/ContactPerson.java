package com.centram.domain;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContactPerson implements Serializable {
    private static final long serialVersionUID = 4271279142878797243L;
    private String name;
    private String email;
    private String contactNo;
}
