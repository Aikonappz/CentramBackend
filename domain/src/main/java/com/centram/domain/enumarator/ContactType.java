package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ContactType {

    MOBILE("MOBILE"),
    EMAIL("EMAIL"),
    IMO("IMO"),
    PHONE("PHONE");

    private final String value;

    ContactType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ContactType fromValue(String text) {
        for (ContactType b : ContactType.values()) {
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
