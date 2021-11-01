package com.centram.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BankDetail implements Serializable {
    private static final long serialVersionUID = -7561599749703058329L;
    private String bankName;
    private String branchName;
    private String acNo;
    private String ifscCode;
    private Boolean primaryBank;
}