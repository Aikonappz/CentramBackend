package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    CASH("CASH"),
    CHEQUE("CHEQUE"),
    ONLINE("ONLINE"),;

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PaymentType fromValue(String text) {
        for (PaymentType b : PaymentType.values()) {
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
