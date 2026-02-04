package com.centram.core.service;

import com.centram.core.repository.JobPostingRepository;
import com.centram.core.repository.RequisitionRepository;
import com.centram.domain.JobPosting;
import com.centram.domain.Requisition;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final RequisitionRepository requisitionRepository;

    public JobPostingService(JobPostingRepository jobPostingRepository,
                             RequisitionRepository requisitionRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.requisitionRepository = requisitionRepository;
    }


    @Transactional
    public JobPosting postJob(BigInteger requisitionId) {

        if (jobPostingRepository.existsByRequisitionId(requisitionId)) {
            throw new RuntimeException(
                    "Job already posted for requisition id: " + requisitionId
            );
        }

        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        JobPosting jobPosting = JobPosting.builder()
                .requisitionId(requisition.getId())
                .jobTitle(requisition.getJobTitle())
                .locationId(requisition.getLocationId())
                .postingStartDate(requisition.getJobPostingStartDate())
                .postingEndDate(requisition.getJobPostingEndDate())
                .postingType(requisition.getJobPostingType())
                .postingBoard(requisition.getJobPostingBoard())
                .postingStatus("POSTED")
                .build();

        return jobPostingRepository.save(jobPosting);
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    public JobPosting getJobPostStatus(BigInteger requisitionId) {
        return jobPostingRepository.findByRequisitionId(requisitionId);
    }

}
