package com.centram.core.repository;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface DtoEntityMapper<T, D> {
    T toEntity(D dto);
    D toDto(T entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(D source, @MappingTarget T target);
}
