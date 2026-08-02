package com.centram.core.service;

import com.centram.common.dto.WorkExperience;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExperienceParser {

    private static final Pattern DATE_RANGE = Pattern.compile(
            "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|January|February|March|April|May|June|July|August|September|October|November|December)?\\s?\\d{4}\\s*[–\\-to]+\\s*(Present|Now|(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)?\\s?\\d{4})",
            Pattern.CASE_INSENSITIVE
    );

    public List<WorkExperience> parse(String section) {

        if (section == null || section.isBlank())
            return Collections.emptyList();

        List<String> blocks = splitExperienceBlocks(section);

        List<WorkExperience> experiences = new ArrayList<>();

        for (String block : blocks) {

            WorkExperience exp = parseBlock(block);

            if (exp != null)
                experiences.add(exp);
        }

        return experiences;
    }

    private List<String> splitExperienceBlocks(String text) {

        List<String> blocks = new ArrayList<>();

        Matcher matcher = DATE_RANGE.matcher(text);

        List<Integer> indices = new ArrayList<>();

        while (matcher.find()) {
            indices.add(matcher.start());
        }

        if (indices.isEmpty()) {

            blocks.add(text);
            return blocks;
        }

        for (int i = 0; i < indices.size(); i++) {

            int start = indices.get(i);
            int end = (i + 1 < indices.size()) ? indices.get(i + 1) : text.length();

            blocks.add(text.substring(start, end));
        }

        return blocks;
    }

    private WorkExperience parseBlock(String block) {

        Matcher matcher = DATE_RANGE.matcher(block);

        if (!matcher.find())
            return null;

        String dateRange = matcher.group();

        String[] parts = dateRange.split("[–\\-to]+");

        LocalDate startDate = parseDate(parts[0].trim());

        LocalDate endDate;

        if (parts[1].toLowerCase().contains("present")
                || parts[1].toLowerCase().contains("now"))
            endDate = LocalDate.now();
        else
            endDate = parseDate(parts[1].trim());

        WorkExperience exp = new WorkExperience();

        exp.setStartDate(startDate);
        exp.setEndDate(endDate);
        exp.setRawDescription(block.trim());

        if (startDate != null && endDate != null) {

            long months = ChronoUnit.MONTHS.between(
                    YearMonth.from(startDate),
                    YearMonth.from(endDate)
            );

            exp.setDurationMonths((int) months);
        }

        extractCompanyAndRole(block, exp);

        return exp;
    }

    private void extractCompanyAndRole(String block, WorkExperience exp) {

        String[] lines = block.split("\\n");

        for (String line : lines) {

            String cleaned = line.trim();

            if (cleaned.isEmpty())
                continue;

            if (exp.getDesignation() == null &&
                    cleaned.matches("(?i).*(engineer|developer|manager|analyst|consultant|lead|architect).*")) {

                exp.setDesignation(cleaned);
                continue;
            }

            if (exp.getCompany() == null &&
                    !cleaned.matches(".*\\d{4}.*") &&
                    cleaned.length() < 100) {

                exp.setCompany(cleaned);
            }
        }
    }

    private LocalDate parseDate(String text) {

        try {

            String[] parts = text.split(" ");

            if (parts.length == 2) {

                int year = Integer.parseInt(parts[1]);
                int month = month(parts[0]);

                return LocalDate.of(year, month, 1);
            }

            if (parts.length == 1) {

                int year = Integer.parseInt(parts[0]);

                return LocalDate.of(year, 1, 1);
            }

        } catch (Exception ignored) {}

        return null;
    }

    private int month(String m) {

        switch (m.toLowerCase()) {

            case "jan": case "january": return 1;
            case "feb": case "february": return 2;
            case "mar": case "march": return 3;
            case "apr": case "april": return 4;
            case "may": return 5;
            case "jun": case "june": return 6;
            case "jul": case "july": return 7;
            case "aug": case "august": return 8;
            case "sep": case "september": return 9;
            case "oct": case "october": return 10;
            case "nov": case "november": return 11;
            case "dec": case "december": return 12;
        }

        return 1;
    }

    public int calculateTotalExperienceMonths(String section) {

        List<WorkExperience> list = parse(section);

        return list.stream()
                .map(WorkExperience::getDurationMonths)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }
}