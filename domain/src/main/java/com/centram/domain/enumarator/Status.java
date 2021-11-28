package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    //ENTITY STATUS
    INACTIVE("INACTIVE"),
    ACTIVE("ACTIVE"),
    ALL("ALL"),
    //ENTITY STATUS
    //NOTIFICATION STATUS
    PUSHED("PUSHED"),
    PULLED("PULLED"),
    VISITED("VISITED"),
    ALREADY_ACTION_TAKEN("ALREADY_ACTION_TAKEN")
    //NOTIFICATION STATUS
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Status fromValue(String text) {
        for (Status b : Status.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException(text + " is not a valid PropName");
    }

    @JsonCreator
    public static Status fromKey(Integer ordinal) {
        for (Status b : Status.values()) {
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