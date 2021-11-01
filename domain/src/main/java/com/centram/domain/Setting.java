package com.centram.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Setting implements Serializable {
    private static final long serialVersionUID = 2149040960400918629L;
    private String itemPrefix;
    private String orderPrefix;
    private String salePrefix;
    private String returnPrefix;
    private String billPrefix;
    private String tin;
    private String gstn;
    @JsonIgnore
    private Integer leadigNoOfCharacter = 10;
    @JsonIgnore
    private String leadigCharacter = "0";
}
