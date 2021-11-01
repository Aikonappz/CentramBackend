package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IdentityType {
    RESIDENTIAL("RESIDENTIAL IDENTITY"),
    GOVT_IDENTITY("GOVT IDENTITY");

    private final String value;

    IdentityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static IdentityType fromValue(String text) {
        for (IdentityType b : IdentityType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
