package com.centram.domain;

import com.centram.domain.enumarator.Identity;
import com.centram.domain.enumarator.IdentityType;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GovtIdentities implements Serializable {

    
    @NotNull
    private IdentityType identityType;

    
    @NotNull
    private Identity identity;

    
    @NotNull
    private String value;
}
