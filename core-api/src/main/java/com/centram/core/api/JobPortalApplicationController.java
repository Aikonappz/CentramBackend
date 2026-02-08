package com.centram.core.api;


import com.centram.core.service.JobPortalApplicationService;
import com.centram.domain.JobPortalApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/job-portal")
public class JobPortalApplicationController {

    private final JobPortalApplicationService jobApplicationService;

    public JobPortalApplicationController(JobPortalApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyJob(@RequestParam BigInteger candidateId, @RequestParam BigInteger jobPostingId) {
        JobPortalApplication application = jobApplicationService.applyJob(candidateId, jobPostingId);
        return ResponseEntity.ok(Map.of("applicationId", application.getId(), "status", application.getApplicationStatus()));
    }
}