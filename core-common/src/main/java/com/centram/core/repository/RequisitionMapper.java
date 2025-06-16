package com.centram.core.repository;

import com.centram.domain.Requisition;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RequisitionMapper {
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequisitionFromRequest(@MappingTarget Requisition target, Requisition source);
}
