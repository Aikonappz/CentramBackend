package com.centram.core.api;

import com.centram.core.service.JobPostingService;
import com.centram.domain.JobPosting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/v1/job-postings")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }


    @PostMapping("/{requisitionId}")
    public ResponseEntity<JobPosting> postJob(@PathVariable BigInteger requisitionId) {

        return ResponseEntity.ok(
                jobPostingService.postJob(requisitionId)
        );
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<JobPosting>> getAllJobPostings() {
        return ResponseEntity.ok(jobPostingService.getAllJobPostings());
    }


    @GetMapping("/{requisitionId}")
    public ResponseEntity<List<JobPosting>> getByRequisition(@PathVariable BigInteger requisitionId) {
        return ResponseEntity.ok(jobPostingService.getByRequisitionId(requisitionId));
    }
}
