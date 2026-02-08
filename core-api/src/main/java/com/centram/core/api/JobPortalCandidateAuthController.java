package com.centram.core.api;


import com.centram.common.dto.AuthRequestDTO;
import com.centram.core.service.JobPortalCandidateAuthService;
import com.centram.domain.JobPortalCandidate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/job-portal")
public class JobPortalCandidateAuthController {

    private final JobPortalCandidateAuthService jobPortalCandidateAuthService;

    public JobPortalCandidateAuthController(JobPortalCandidateAuthService jobPortalCandidateAuthService) {
        this.jobPortalCandidateAuthService = jobPortalCandidateAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequestDTO request) {
        JobPortalCandidate candidate = jobPortalCandidateAuthService.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of("message", "Registration successful", "candidateId", candidate.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        JobPortalCandidate candidate = jobPortalCandidateAuthService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "candidateId", candidate.getId(),
                        "email", candidate.getEmail()));
    }
}
