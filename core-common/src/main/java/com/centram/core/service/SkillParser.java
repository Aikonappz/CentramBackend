package com.centram.core.service;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SkillParser {

    private static final Set<String> SKILL_DICTIONARY = Set.of(
            "Java", "Spring Boot", "Microservices",
            "Kafka", "Docker", "Kubernetes",
            "AWS", "SQL", "Angular", "React"
    );

    public Set<String> parse(String text) {

        String lower = text.toLowerCase();

        return SKILL_DICTIONARY.stream()
                .filter(skill ->
                        lower.contains(skill.toLowerCase()))
                .collect(Collectors.toSet());
    }
}
