package com.centram.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer totalExperienceMonths;
    private String totalExperience;
    private List<WorkExperience> experiences;
    private List<EducationDetail> education;
    private Set<String> skills;
}
