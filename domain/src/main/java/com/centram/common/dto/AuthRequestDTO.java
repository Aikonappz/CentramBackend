package com.centram.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRequestDTO implements Serializable {
    private static final long serialVersionUID = -1177813685524269476L;
    private String username;
    private String password;
    private Boolean rememberMe;
    private String email;
}