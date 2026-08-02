package com.centram.core.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SectionSplitter {

    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "^(.*?(experience|employment|work history|professional experience|" +
                    "education|academic|qualification|" +
                    "skills|technical skills|core skills|" +
                    "projects|project experience)).*$",
            Pattern.CASE_INSENSITIVE);

    public Map<String, String> split(String text) {

        Map<String, String> sections = new HashMap<>();

        String[] lines = text.split("\\r?\\n");

        String currentSection = "general";
        StringBuilder buffer = new StringBuilder();

        for (String line : lines) {

            String cleaned = line.trim();

            Matcher matcher = SECTION_PATTERN.matcher(cleaned);

            if (matcher.find()) {

                sections.put(currentSection, buffer.toString().trim());

                currentSection = normalizeSection(matcher.group(2));

                buffer = new StringBuilder();

            } else {

                buffer.append(line).append("\n");

            }
        }

        sections.put(currentSection, buffer.toString().trim());

        return sections;
    }

    private String normalizeSection(String header) {

        header = header.toLowerCase();

        if (header.contains("experience") || header.contains("employment"))
            return "experience";

        if (header.contains("education") || header.contains("academic"))
            return "education";

        if (header.contains("skill"))
            return "skills";

        if (header.contains("project"))
            return "projects";

        return "general";
    }
}