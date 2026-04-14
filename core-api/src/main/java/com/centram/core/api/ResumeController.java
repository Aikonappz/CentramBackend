package com.centram.core.api;


import com.centram.common.dto.ResumeProfileResponse;
import com.centram.core.service.ResumeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    ResumeApplicationService resumeApplicationService;

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeProfileResponse> upload(@RequestParam("file") MultipartFile file) {
        ResumeProfileResponse response = resumeApplicationService.process(file);
        return ResponseEntity.ok(response);
    }
}
