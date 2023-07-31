package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LicenseType {

    ALL("ALL"),
    INCIDENT("INCIDENT"),
    ASSET("ASSET"),
    PROJECT("PROJECT"),
    UAT("UAT");

    private final String value;

    LicenseType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static LicenseType fromValue(String text) {
        for (LicenseType b : LicenseType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException(text + " is not a valid PropName");

    }

    @JsonCreator
    public static LicenseType fromKey(Integer ordinal) {
        for (LicenseType b : LicenseType.values()) {
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