package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaType {
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

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
