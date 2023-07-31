package com.centram.domain.composite.key;


import com.centram.domain.Project;
import com.centram.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDate;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSheetId implements Serializable {
    private LocalDate startDate;
    private LocalDate endDate;
    private Project project;
    private User user;
}
