package com.centram.core.repository;

import com.centram.domain.Position;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionMapper extends GenericMapStructMapper<Position> {
}
