package com.centram.core.repository;

import com.centram.common.dto.JobProfileDTO;
import com.centram.domain.Competency;
import com.centram.domain.JobProfile;
import org.mapstruct.*;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JobProfileMapper extends DtoEntityMapper<JobProfile, JobProfileDTO> {

    @Override
    @Mapping(source = "jobRole.id", target = "jobRoleId")
    @Mapping(target = "competencyIds", expression = "java(mapCompetencyIds(entity.getCompetencies()))")
    JobProfileDTO toDto(JobProfile entity);

    // DTO -> Entity
    @Override
    @Mapping(source = "jobRoleId", target = "jobRole.id")
    @Mapping(target = "competencies", ignore = true) // handled manually in service if needed
    JobProfile toEntity(JobProfileDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(JobProfileDTO source, @MappingTarget JobProfile target);

    default List<BigInteger> mapCompetencyIds(List<Competency> competencies) {
        return competencies == null ? null :
                competencies.stream()
                        .map(Competency::getId)
                        .collect(Collectors.toList());
    }
}
