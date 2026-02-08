package com.centram.core.service;

import com.centram.core.repository.JobPortalApplicationRepository;
import com.centram.domain.JobPortalApplication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;

@Service
public class JobPortalApplicationService {

    private final JobPortalApplicationRepository jobPortalApplicationRepository;

    public JobPortalApplicationService(JobPortalApplicationRepository jobPortalApplicationRepository) {
        this.jobPortalApplicationRepository = jobPortalApplicationRepository;
    }

    @Transactional
    public JobPortalApplication applyJob(BigInteger candidateId, BigInteger jobPostingId) {

        if (jobPortalApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)) {
            throw new RuntimeException("Already applied for this job");
        }

        JobPortalApplication application = new JobPortalApplication();
        application.setCandidateId(candidateId);
        application.setJobPostingId(jobPostingId);
        application.setApplicationStatus("APPLIED");

        return jobPortalApplicationRepository.save(application);
    }
}
