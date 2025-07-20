package com.centram.core.repository;

import com.centram.common.dto.JobFamilyDTO;
import com.centram.domain.JobFamily;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobFamilyMapper extends DtoEntityMapper<JobFamily, JobFamilyDTO> {
}
