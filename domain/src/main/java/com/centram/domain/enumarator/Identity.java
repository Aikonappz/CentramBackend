package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Identity {
    AADHAR("AADHAR"),
    DRIVING_LICENSE("DRIVING LICENSE"),
    PAN("PAN"),
    PASSPORT("PASSPORT"),
    ELECTRICITY("ELECTRICITY"),
    VOTER("VOTER");

    private final String value;

    Identity(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Identity fromValue(String text) {
        for (Identity b : Identity.values()) {
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
