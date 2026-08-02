package com.centram.core.service;

import com.centram.common.dto.BasicInfo;
import com.centram.common.dto.ResumeProfile;
import com.centram.common.dto.WorkExperience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ResumeParsingEngine {

    @Autowired
    SectionSplitter sectionSplitter;
    @Autowired
    ExperienceParser experienceParser;
    @Autowired
    EducationParser educationParser;
    @Autowired
    SkillParser skillParser;
    @Autowired
    HeaderParser headerParser;

    public ResumeProfile parse(String rawText) {

        BasicInfo basicInfo = headerParser.parse(rawText);

        // 2️⃣ Split resume into sections
        Map<String, String> sections = sectionSplitter.split(rawText);

        // 3️⃣ Parse experiences
        List<WorkExperience> experiences = experienceParser.parse(sections.get("experience"));

        // 4️⃣ Calculate total experience months
        int totalMonths = experienceParser.calculateTotalExperienceMonths(sections.get("experience"));

        // 5️⃣ Parse education
        var education = educationParser.parse(sections.get("education"));

        // 6️⃣ Parse skills
        var skills = skillParser.parse(rawText);

        // 7️⃣ Build final profile
        return ResumeProfile.builder()
                .firstName(basicInfo.getFirstName())
                .lastName(basicInfo.getLastName())
                .email(basicInfo.getEmail())
                .phone(basicInfo.getPhone())
                .experiences(experiences)
                .education(education)
                .skills(skills)
                .totalExperienceMonths(totalMonths)
                .build();
    }
}
