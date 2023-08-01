package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(Views.BasicView.class)
    private String name;
    @JsonView(Views.BasicView.class)
    private String email;
    @JsonView(Views.BasicView.class)
    private String comment;
}