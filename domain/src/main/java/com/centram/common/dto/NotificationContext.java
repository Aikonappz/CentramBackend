package com.centram.common.dto;

import com.centram.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class NotificationContext {
    private final User user;
    private final Map<String, String> placeholders;
    private final String templateKey;
}
