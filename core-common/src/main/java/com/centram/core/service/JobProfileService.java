package com.centram.core.service;

import com.centram.common.dto.*;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.*;
import com.centram.domain.Competency;
import com.centram.domain.JobFamily;
import com.centram.domain.JobProfile;
import com.centram.domain.JobRole;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobProfileService {

    @Autowired
    JobFamilyRepository jobFamilyRepository;
    @Autowired
    JobRoleRepository jobRoleRepository;
    @Autowired
    CompetencyRepository competencyRepository;
    @Autowired
    JobProfileRepository jobProfileRepository;
    @Autowired
    JobFamilyMapper jobFamilyMapper;
    @Autowired
    JobRoleMapper jobRoleMapper;
    @Autowired
    CompetencyMapper competencyMapper;
    @Autowired
    JobProfileMapper jobProfileMapper;

    @Transactional
    public JobFamilyDTO saveJobFamily(JobFamilyDTO dto) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobFamily entity = dto.getId() != null ?
                jobFamilyRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("JobFamily not found")) :
                new JobFamily();
        jobFamilyMapper.updateFromDto(dto, entity);
        return jobFamilyMapper.toDto(jobFamilyRepository.save(entity));
    }

    public JobFamilyDTO getJobFamily(BigInteger id) {
        return jobFamilyMapper.toDto(jobFamilyRepository.findById(id).orElseThrow());
    }

    @Transactional
    public JobRoleDTO saveJobRole(JobRoleDTO dto) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobRole entity = dto.getId() != null ?
                jobRoleRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("JobRole not found")) :
                new JobRole();
        jobRoleMapper.updateFromDto(dto, entity);
        entity.setJobFamily(jobFamilyRepository.findById(dto.getJobFamilyId()).orElseThrow());
        return jobRoleMapper.toDto(jobRoleRepository.save(entity));
    }

    public JobRoleDTO getJobRole(BigInteger id) {
        return jobRoleMapper.toDto(jobRoleRepository.findById(id).orElseThrow());
    }

    @Transactional
    public CompetencyDTO saveCompetency(CompetencyDTO dto) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Competency entity = dto.getId() != null ?
                competencyRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Competency not found")) :
                new Competency();
        competencyMapper.updateFromDto(dto, entity);
        entity.setJobRole(jobRoleRepository.findById(dto.getJobRoleId()).orElseThrow());
        return competencyMapper.toDto(competencyRepository.save(entity));
    }

    public CompetencyDTO getCompetency(BigInteger id) {
        return competencyMapper.toDto(competencyRepository.findById(id).orElseThrow());
    }

    @Transactional
    public JobProfileDTO saveJobProfile(JobProfileDTO dto) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobProfile entity = dto.getId() != null ?
                jobProfileRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("JobProfile not found")) :
                new JobProfile();
        jobProfileMapper.updateFromDto(dto, entity);
        entity.setJobRole(jobRoleRepository.findById(dto.getJobRoleId()).orElseThrow());
        entity.setCompetencies(competencyRepository.findAllById(dto.getCompetencyIds()));
        return jobProfileMapper.toDto(jobProfileRepository.save(entity));
    }

    public JobProfileDTO getJobProfile(BigInteger id) {
        return jobProfileMapper.toDto(jobProfileRepository.findById(id).orElseThrow());
    }

    public List<JobProfileResponseDTO> getJobProfilesByJobCode(String jobCodeId) {
        List<JobProfile> profiles = jobProfileRepository.findByJobCodeId(jobCodeId);

        return profiles.stream().map(profile -> {
            JobProfileResponseDTO dto = new JobProfileResponseDTO();
            BeanUtils.copyProperties(profile, dto);
            List<CompetencyBasicDTO> competencyDtos = profile.getCompetencies().stream()
                    .map(c -> new CompetencyBasicDTO(c.getId(), c.getCompetencyName()))
                    .collect(Collectors.toList());
            dto.setCompetencies(competencyDtos);
            JobRole role = profile.getJobRole();
            if (role != null) {
                dto.setJobRoleId(role.getId());
                dto.setJobRoleName(role.getJobRoleName());
                dto.setJobCodeId(role.getJobCodeId());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public PaginatedList<JobFamilyDTO> getAllJobFamilies(Pageable pageable) {
        var page = jobFamilyRepository.findAll(pageable)
                .map(jobFamilyMapper::toDto);
        return new PaginatedList<>(page);
    }

    public PaginatedList<JobRoleDTO> getAllJobRoles(Pageable pageable) {
        var page = jobRoleRepository.findAll(pageable)
                .map(jobRoleMapper::toDto);
        return new PaginatedList<>(page);
    }

    public PaginatedList<CompetencyDTO> getAllCompetencies(Pageable pageable) {
        var page = competencyRepository.findAll(pageable)
                .map(competencyMapper::toDto);
        return new PaginatedList<>(page);
    }

    public PaginatedList<JobProfileDTO> getAllJobProfiles(Pageable pageable) {
        var page = jobProfileRepository.findAll(pageable)
                .map(jobProfileMapper::toDto);
        return new PaginatedList<>(page);
    }


}
