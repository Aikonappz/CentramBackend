package com.centram.common.exeception;

import org.slf4j.MDC;

import java.time.LocalDateTime;

public class ClientError {
    private final String token;
    private final LocalDateTime timestamp;
    private final String message;
    private String code;

    ClientError(String message) {
        this.message = message;
        this.token = MDC.get("correlation-id");
        this.timestamp = LocalDateTime.now();
    }

    public ClientError(String code, String message) {
        this.message = message;
        this.token = MDC.get("correlation-id");
        this.timestamp = LocalDateTime.now();
        this.code = code;
    }

    public ClientError() {
        this.token = null;
        this.message = null;
        this.timestamp = null;
        this.code = null;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
