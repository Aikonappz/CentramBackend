package com.centram.core.repository;

import com.centram.domain.Requisition;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RequisitionMapper extends GenericMapStructMapper<Requisition> {
}
