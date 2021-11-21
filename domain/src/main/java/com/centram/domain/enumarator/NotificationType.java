package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    INFO("INFO NOTIFICATION"),
    ACTIONABLE("ACTIONABLE NOTIFICATION");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static NotificationType fromValue(String text) {
        for (NotificationType b : NotificationType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonCreator
    public static NotificationType fromKey(Integer ordinal) {
        for (NotificationType b : NotificationType.values()) {
            if (b.ordinal() == ordinal) {
                return b;
            }
        }
        throw new IllegalArgumentException(ordinal + " is not a valid PropValue");
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}