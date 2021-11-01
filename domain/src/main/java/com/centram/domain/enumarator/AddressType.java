package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AddressType {
    BUSINESS("BUSINESS"),
    RESIDENTIAL("RESIDENTIAL"),
    WORK("WORK"),
    PERMANENT("PERMANENT");

    private final String value;

    AddressType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AddressType fromValue(String text) {
        for (AddressType b : AddressType.values()) {
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
