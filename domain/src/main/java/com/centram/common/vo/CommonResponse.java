package com.centram.common.vo;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommonResponse implements Serializable {
    private static final long serialVersionUID = 2547741160092421414L;
    private Boolean status;
    private String message;
}