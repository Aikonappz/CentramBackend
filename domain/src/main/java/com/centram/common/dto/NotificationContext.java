package com.centram.common.dto;

import com.centram.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationContext {
    private final User user;
    private final String title;
    private final String content;
}
