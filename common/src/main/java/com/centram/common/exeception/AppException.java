package com.centram.common.exeception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * The Class RESTException.
 */
public class AppException extends RuntimeException {
    private ErrorCode code;
    private final Instant occurredAt;
    private Map<String, Object> context = new HashMap<>();

    public AppException(final ErrorCode code) {
        super();
        this.code = code;
        occurredAt = Instant.now();
    }

    public AppException(final String message) {
        super(message);
        occurredAt = Instant.now();
    }

    public AppException(final ErrorCode code, final Throwable throwable) {
        super(throwable);
        this.code = code;
        occurredAt = Instant.now();
    }

    public AppException(final ErrorCode code, final Throwable throwable, final Map<String, Object> context) {
        super(code.getCode(), throwable);
        this.context = context;
        occurredAt = Instant.now();
    }

    public AppException(final ErrorCode code, final Map<String, Object> context, final Throwable throwable) {
        super(code.getCode(), throwable);
        this.context = context;
        this.code = code;
        occurredAt = Instant.now();
    }

    public AppException(final ErrorCode code, final Map<String, Object> context) {
        super(code.getCode());
        this.context = context;
        occurredAt = Instant.now();
    }

    public ErrorCode getCode() {
        return code;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
