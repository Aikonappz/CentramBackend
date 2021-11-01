package com.centram.common.vo;


import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ActionVO implements Serializable {
    private static final long serialVersionUID = -6554446568157662441L;
    private BigInteger id;
    private String name;
}