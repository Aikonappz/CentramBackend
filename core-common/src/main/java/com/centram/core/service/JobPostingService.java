package com.centram.core.service;

import com.centram.common.dto.JobPostingDto;
import com.centram.common.dto.JobPostingResponseDto;
import com.centram.core.repository.JobPostingRepository;
import com.centram.core.repository.RequisitionRepository;
import com.centram.domain.JobPosting;
import com.centram.domain.Requisition;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public JobPostingResponseDto postJob(JobPostingDto jobPostingDto) {

        if (jobPostingRepository.existsByRequisitionId(jobPostingDto.getRequisitionId())) {
            throw new RuntimeException("Job already posted for requisition id: " + jobPostingDto.getRequisitionId());
        }

        Requisition requisition = requisitionRepository.findById(jobPostingDto.getRequisitionId())
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        JobPosting jobPosting = JobPosting.builder()
                .requisitionId(requisition.getId())
                .jobTitle(requisition.getJobTitle())
                .locationId(requisition.getLocationId())
                .postingStartDate(jobPostingDto.getJobPortalPostingStartDate())
                .postingEndDate(jobPostingDto.getJobPortalPostingEndDate())
                .postingType(requisition.getJobPostingType())
                .postingBoard(jobPostingDto.getJobPortalCareerSite())
                .postingStatus("POSTED")
                .repostAfterExpiration(jobPostingDto.isRepostAfterExpiration())
                .build();

        JobPosting result = jobPostingRepository.save(jobPosting);
        return mapToResponseDto(result);
    }

    public List<JobPostingResponseDto> getAllJobPostings() {
        return jobPostingRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public JobPostingResponseDto getJobPostStatus(BigInteger requisitionId) {
        Optional<JobPosting> jobPosting = jobPostingRepository.findByRequisitionId(requisitionId);
        if(jobPosting.isPresent()) return mapToResponseDto(jobPosting.get());
        return new JobPostingResponseDto();
    }

    private JobPostingResponseDto mapToResponseDto(JobPosting result) {
        return JobPostingResponseDto.builder()
                .id(result.getId())
                .requisitionId(result.getRequisitionId())
                .jobTitle(result.getJobTitle())
                .locationId(result.getLocationId())
                .jobPortalPostingStartDate(result.getPostingStartDate())
                .jobPortalPostingEndDate(result.getPostingEndDate())
                .jobPortalCareerSite(result.getPostingBoard())
                .postingStatus(result.getPostingStatus())
                .repostAfterExpiration(result.isRepostAfterExpiration())
                .build();
    }

    public JobPostingResponseDto updateJobPosting(JobPostingDto dto) {

        Optional<JobPosting> jobPosting = jobPostingRepository.findByRequisitionId(dto.getRequisitionId());
        if(jobPosting.isEmpty()) throw new RuntimeException("Job already posted for requisition id: " + dto.getRequisitionId());
        JobPosting entity = jobPosting.get();
        if (dto.getJobPortalPostingStartDate() != null) {
            entity.setPostingStartDate(dto.getJobPortalPostingStartDate());
        }

        if (dto.getJobPortalPostingEndDate() != null) {
            entity.setPostingEndDate(dto.getJobPortalPostingEndDate());
        }

        if (dto.getJobPortalCareerSite() != null) {
            entity.setPostingBoard(dto.getJobPortalCareerSite());
        }

        entity.setRepostAfterExpiration(dto.isRepostAfterExpiration());

        JobPosting saved = jobPostingRepository.save(entity);

        return mapToResponseDto(saved);
    }

}
