package com.centram.core.repository;

import com.centram.common.dto.JobRoleDTO;
import com.centram.domain.JobRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobRoleMapper extends DtoEntityMapper<JobRole, JobRoleDTO> {
}
