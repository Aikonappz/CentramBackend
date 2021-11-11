package com.centram.domain.enumarator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityType {
    SIGNIN("APP SIGN IN"),
    SIGNOUT("APP SIGN OUT"),
    FORGOT_PASSWORD("APP FORGOT PASSWORD"),
    RESET_PASSWORD("APP RESET PASSWORD"),
    LOGOUT("APP LOG OUT"),
    ORGANISATION_ONBOARD("ORGANISATION ONBOARD"),
    UPDATE_ORGANISATION("ORGANISATION UPDATE"),
    ORGANISATION_LOGO_UPLOAD("UPLOAD ORGANISATION LOGO"),
    ADD_USER("USER ADD"),
    UPDATE_USER("USER UPDATE"),
    USER_PROFILE_PHOTO_UPLOAD("UPLOAD ORGANISATION LOGO"),
    ADD_CATEGORY("CATEGORY ADD"),
    UPDATE_CATEGORY("CATEGORY UPDATE"),
    ADD_STOCK("ADD ITEM IN STOCK"),
    UPDATE_STOCK("UPDATE ITEM IN STOCK"),
    ADD_CUSTOMER("ADD CUSTOMER"),
    UPDATE_CUSTOMER("UPDATE CUSTOMER");


    private final String value;

    ActivityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActivityType fromValue(String text) {
        for (ActivityType b : ActivityType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonCreator
    public static ActivityType fromKey(Integer ordinal) {
        for (ActivityType b : ActivityType.values()) {
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
