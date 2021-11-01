package com.centram.domain;

import com.centram.domain.enumarator.Identity;
import com.centram.domain.enumarator.IdentityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-05-20T12:19:48.018Z")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GovtIdentities implements Serializable {

    @ApiModelProperty(required = true, value = "")
    @NotNull
    private IdentityType identityType;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    private Identity identity;

    @ApiModelProperty(required = true, value = "")
    @NotNull
    private String value;
}
