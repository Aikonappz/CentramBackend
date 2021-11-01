package com.centram.common.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AuthRequestDTO implements Serializable {
    private static final long serialVersionUID = -1177813685524269476L;
    private String username;
    private String password;
    private Boolean rememberMe;
}