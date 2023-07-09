package com.centram.domain;

import com.centram.common.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TimeEntry implements Serializable {
    private static final long serialVersionUID = -2687300170921044181L;
    @JsonView(Views.BasicView.class)
    private String purpose;
    @JsonView(Views.BasicView.class)
    private String time;
    @JsonView(Views.BasicView.class)
    private Boolean newEntry;
}