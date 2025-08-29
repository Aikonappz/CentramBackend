package com.centram.core.repository;

import com.centram.common.dto.NotificationContext;

import java.util.List;

public interface NotificationExtractor<T> {
    List<NotificationContext> extract(T source, String status);
}
