package com.centram.core.repository;

import com.centram.common.dto.CompetencyDTO;
import com.centram.domain.Competency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompetencyMapper extends DtoEntityMapper<Competency, CompetencyDTO> {
}
