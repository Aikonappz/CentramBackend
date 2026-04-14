package com.centram.core.service;

import org.springframework.stereotype.Component;

@Component
public class ResumePreProcessor {

    public String clean(String text) {

        text = text.replaceAll("[•●■]", " ");
        text = text.replaceAll("\\t", " ");
        text = text.replaceAll(" +", " ");
        text = text.replaceAll("\\n+", "\n");

        return text.trim();
    }
}
