package com.centram.core.repository;

import com.centram.common.dto.JobProfileDTO;
import com.centram.domain.JobProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobProfileMapper extends DtoEntityMapper<JobProfile, JobProfileDTO> {
}
