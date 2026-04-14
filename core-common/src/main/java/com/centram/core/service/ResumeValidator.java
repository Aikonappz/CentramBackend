package com.centram.core.service;

import com.centram.common.exception.ResumeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ResumeValidator {

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty())
            throw new ResumeException("Resume file is empty");

        if (file.getSize() > MAX_SIZE)
            throw new ResumeException("File exceeds 5MB limit");

        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new ResumeException("Unsupported file type");
    }
}
