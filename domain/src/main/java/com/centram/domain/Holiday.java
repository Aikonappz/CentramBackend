package com.centram.domain;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDate;

@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Holiday implements Serializable {
    private static final long serialVersionUID = -7182599749703058329L;
    private LocalDate date;
    private String description;
}