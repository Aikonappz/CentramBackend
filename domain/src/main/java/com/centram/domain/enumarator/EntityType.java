package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EntityType {
    ORGANISATION("ORGANISATION"),
    USER("USER"),
    ITEM("ITEM"),
    SALE("SALE"),
    ODER("ODER");

    private final String value;

    EntityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EntityType fromValue(String text) {
        for (EntityType b : EntityType.values()) {
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
