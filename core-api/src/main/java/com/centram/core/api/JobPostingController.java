package com.centram.core.api;

import com.centram.common.dto.JobPostingDto;
import com.centram.common.dto.JobPostingResponseDto;
import com.centram.core.service.JobPostingService;
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


    @PostMapping("/save")
    public ResponseEntity<JobPostingResponseDto> postJob(@RequestBody JobPostingDto jobPostingDto) {
        return ResponseEntity.ok(jobPostingService.postJob(jobPostingDto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<JobPostingResponseDto>> getAllJobPostings() {
        return ResponseEntity.ok(jobPostingService.getAllJobPostings());
    }

    @GetMapping("/get-status/{requisitionId}")
    public ResponseEntity<JobPostingResponseDto> getJobPostStatus(@PathVariable BigInteger requisitionId) {
        return ResponseEntity.ok(jobPostingService.getJobPostStatus(requisitionId));
    }

    @PutMapping("/update")
    public ResponseEntity<JobPostingResponseDto> updateJobPosting(@RequestBody JobPostingDto dto) {
        return ResponseEntity.ok(jobPostingService.updateJobPosting(dto));
    }
}
