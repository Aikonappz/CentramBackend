package com.centram.core.service;

import com.centram.core.repository.GenericMapStructMapper;

public class EntityUpdateService {

    private EntityUpdateService(){}

    public static <T> T updateEntity(T source, T target, GenericMapStructMapper<T> mapper) {
        mapper.update(target, source);
        return target;
    }

}
