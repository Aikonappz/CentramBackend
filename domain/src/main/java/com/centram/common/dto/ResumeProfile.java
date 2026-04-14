package com.centram.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ResumeProfile {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer totalExperienceMonths;
    private List<WorkExperience> experiences;
    private List<EducationDetail> education;
    private Set<String> skills;
}
