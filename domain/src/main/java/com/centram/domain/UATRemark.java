package com.centram.domain;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UATRemark implements Serializable {
    private static final long serialVersionUID = 7728943576226702023L;
    private String name;
    private String email;
    private String comment;
    private LocalDateTime dateTime;
}