package com.centram.core.repository;

import com.centram.common.dto.JobRoleDTO;
import com.centram.domain.JobRole;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface JobRoleMapper extends DtoEntityMapper<JobRole, JobRoleDTO> {
    @Override
    @Mapping(source = "jobFamily.id", target = "jobFamilyId")
    JobRoleDTO toDto(JobRole entity);

    @Override
    @Mapping(source = "jobFamilyId", target = "jobFamily.id")
    JobRole toEntity(JobRoleDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(JobRoleDTO source, @MappingTarget JobRole target);
}
