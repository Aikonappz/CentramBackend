package com.centram.common.dto;

public class ResumeMapper {

    public static ResumeProfileResponse toResponse(ResumeProfile profile) {
        ResumeProfileResponse response = new ResumeProfileResponse();
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setEmail(profile.getEmail());
        response.setPhone(profile.getPhone());

        response.setTotalExperienceMonths(
                profile.getTotalExperienceMonths());

        response.setTotalExperience(ExperienceFormatter.format(profile.getTotalExperienceMonths()));

        response.setSkills(profile.getSkills());

        response.setExperiences(profile.getExperiences());

        response.setEducation(profile.getEducation());

        return response;
    }
}
