package com.centram.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated

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