package com.centram.core.service;

import com.centram.common.dto.EducationDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EducationParser {

    private static final Pattern DATE_PATTERN =
            Pattern.compile(
                    "(Jan|January|Feb|February|Mar|March|Apr|April|May|Jun|June|Jul|July|Aug|August|Sep|September|Oct|October|Nov|November|Dec|December|19\\d{2}|20\\d{2})",
                    Pattern.CASE_INSENSITIVE);

    private static final List<String> DEGREE_KEYWORDS = List.of(
            "bachelor","b.tech","b.e","be","bsc","bca","bcom",
            "master","m.tech","msc","mca","mba","phd"
    );

    private static final List<String> INSTITUTE_KEYWORDS = List.of(
            "college","university","institute","school","academy"
    );

    public List<EducationDetail> parse(String section) {

        if (section == null)
            return List.of();

        List<EducationDetail> list = new ArrayList<>();

        String degree = null;
        String institute = null;

        for (String line : section.split("\n")) {

            String cleaned = line.trim();

            if (cleaned.isBlank())
                continue;

            if (isDegree(cleaned))
                degree = extractDegree(cleaned);

            else if (isInstitute(cleaned))
                institute = extractInstitute(cleaned);
        }

        if (degree != null || institute != null) {

            EducationDetail edu = new EducationDetail();
            edu.setDegree(degree);
            edu.setInstitute(institute);

            list.add(edu);
        }

        return list;
    }

    private String extractDegree(String line) {

        // remove GPA / CGPA numbers
        line = line.replaceAll("\\b\\d+(\\.\\d+)?\\b", "");

        return line.replaceAll("[-–]", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private String extractInstitute(String line) {

        Matcher matcher = DATE_PATTERN.matcher(line);

        if (matcher.find()) {
            line = line.substring(0, matcher.start());
        }

        return line.replaceAll("[-–]", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private boolean isDegree(String text) {

        String lower = text.toLowerCase();

        for (String k : DEGREE_KEYWORDS)
            if (lower.contains(k))
                return true;

        return false;
    }

    private boolean isInstitute(String text) {

        String lower = text.toLowerCase();

        for (String k : INSTITUTE_KEYWORDS)
            if (lower.contains(k))
                return true;

        return false;
    }
}