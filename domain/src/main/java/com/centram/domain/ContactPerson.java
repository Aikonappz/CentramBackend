package com.centram.domain;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
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
