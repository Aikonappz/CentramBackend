package com.centram.common.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestDemoDTO implements Serializable {
    private static final long serialVersionUID = -7191418396599789604L;
    private String name;
    private String email;
    private String phone;
}