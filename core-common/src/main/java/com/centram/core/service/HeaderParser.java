package com.centram.core.service;

import com.centram.common.dto.BasicInfo;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HeaderParser {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(\\+91[- ]?)?[6-9]\\d{9}");

    public BasicInfo parse(String text) {

        String[] lines = text.split("\n");

        String headerBlock = getHeaderBlock(lines);

        BasicInfo info = new BasicInfo();

        info.setEmail(extractEmail(headerBlock));
        info.setPhone(extractPhone(headerBlock));

        setName(info, lines);

        return info;
    }

    private void setName(BasicInfo info, String[] lines) {

        for (int i = 0; i < Math.min(lines.length, 5); i++) {

            String line = lines[i].trim();

            if (line.length() > 3 &&
                    !line.contains("@") &&
                    !line.matches(".*\\d.*")) {

                String[] parts = line.split("\\s+");

                if (parts.length >= 2) {

                    info.setFirstName(parts[0]);
                    info.setLastName(parts[1]);

                } else {

                    info.setFirstName(parts[0]);
                }

                return;
            }
        }
    }

    private String getHeaderBlock(String[] lines) {

        StringBuilder header = new StringBuilder();

        for (int i = 0; i < Math.min(lines.length, 12); i++) {

            header.append(lines[i]).append("\n");
        }

        return header.toString();
    }

    private String extractEmail(String text) {

        Matcher matcher = EMAIL_PATTERN.matcher(text);

        return matcher.find() ? matcher.group() : null;
    }

    private String extractPhone(String text) {

        Matcher matcher = PHONE_PATTERN.matcher(text);

        return matcher.find() ? matcher.group() : null;
    }
}