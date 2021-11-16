package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryType {

    INCIDENT("Incident"),
    ASSET("Asset");

    private final String value;

    CategoryType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CategoryType fromValue(String text) {
        for (CategoryType b : CategoryType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException(text + " is not a valid PropName");

    }

    @JsonCreator
    public static CategoryType fromKey(Integer ordinal) {
        for (CategoryType b : CategoryType.values()) {
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