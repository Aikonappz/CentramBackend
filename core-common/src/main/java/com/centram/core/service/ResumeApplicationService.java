package com.centram.core.service;

import com.centram.common.dto.ResumeMapper;
import com.centram.common.dto.ResumeProfile;
import com.centram.common.dto.ResumeProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeApplicationService {

    @Autowired
    ResumeValidator resumeValidator;
    @Autowired
    ResumeTextExtractionService extractionService;
    @Autowired
    ResumeParsingEngine parsingEngine;
    @Autowired
    ResumePreProcessor preProcessor;

    public ResumeProfileResponse process(MultipartFile file) {
        resumeValidator.validate(file);
        String rawText = extractionService.extract(file);
        String cleanText = preProcessor.clean(rawText);
        ResumeProfile profile = parsingEngine.parse(cleanText);
        return ResumeMapper.toResponse(profile);
    }
}
