package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.print.attribute.standard.Media;

public enum MediaType {
    INCIDENT_COMMUNICATION("INCIDENT COMMUNICATION"),
    ORGANISATION_LOGO_IMAGE("ORGANISATION LOGO IMAGE"),
    USER_PROFILE_IMAGE("USER PROFILE IMAGE"),
    ITEM_IMAGE("ITEM IMAGE"),
    SALE_COPY("SALE COPY"),
    ODER_COPY("ODER COPY");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static MediaType fromValue(String text) {
        for (MediaType b : MediaType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonCreator
    public static MediaType fromKey(Integer ordinal) {
        for (MediaType b : MediaType.values()) {
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
