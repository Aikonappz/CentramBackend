package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Technology {
    SAP_SUCCESS_FACTORS("SAP SuccessFactors"), ORACLE("Oracle"), WORKDAY("Workday");

    private final String value;

    Technology(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Technology fromValue(String text) {
        for (Technology b : Technology.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException(text + " is not a valid PropName");
    }

    @JsonCreator
    public static Technology fromKey(Integer ordinal) {
        for (Technology b : Technology.values()) {
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