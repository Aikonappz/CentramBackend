package com.centram.core.repository;

import com.centram.common.dto.CompetencyDTO;
import com.centram.domain.Competency;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CompetencyMapper extends DtoEntityMapper<Competency, CompetencyDTO> {
    @Override
    @Mapping(source = "jobRole.id", target = "jobRoleId")
    CompetencyDTO toDto(Competency entity);

    @Override
    @Mapping(source = "jobRoleId", target = "jobRole.id")
    Competency toEntity(CompetencyDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "jobRoleId", target = "jobRole.id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(CompetencyDTO source, @MappingTarget Competency target);
}
