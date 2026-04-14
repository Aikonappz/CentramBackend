package com.centram.common.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkExperience {
    private String company;
    private String designation;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationMonths;
    private String rawDescription;
}