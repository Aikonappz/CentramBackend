package com.centram.common.dto;

import lombok.Data;

@Data
public class NotificationRequestDto {

    private String title;
    private String body;
    private String topic;
    private String token;
}