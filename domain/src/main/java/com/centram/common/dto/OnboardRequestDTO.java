package com.centram.common.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OnboardRequestDTO implements Serializable {
    private static final long serialVersionUID = -7191418396599789604L;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}