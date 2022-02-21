package com.centram.common.exeception;

import org.apache.commons.lang3.StringUtils;

public enum GenericErrorCode implements ErrorCode {
    LOGIN_FAILED("Invalid Credentials!"),
    INVALID_REQUEST("Invalid Request!"),
    FILE_UPLOAD_ISSUE("File Upload Issue!"),
    AUTH_FAILURE("Authentication Error!"),
    USER_DISABLED("User Disabled!"),
    INVALID_CREDENTIALS("Invalid Credential!"),
    UNKNOWN_ERROR("Unknown Error!"),
    UNAUTHORIZED("UNAUTHORIZED!"),
    FILE_READ_ISSUE("File read issue!"),
    DATA_NOT_FOUND("Data not found!"),
    JSON_PROCESS_EXCEPTION("Json process exception!"),
    SERIALIZATION_ISSUE("Serialization issue!"),
    DESERIALIZATION_ISSUE("Deserialization issue!"),
    CSV_GENERATION_ISSUE("CSV generation issue!"),
    CSV_PROCESSING_ISSUE("CSV processing issue!"),
    PROFILE_INACTIVE("User profile inactive! Please contact System Admin."),
    HOLIDAY_CALENDER_MASTER_DATA_MISSING("Holiday calender master data missing!"),
    ASSET_DATA_EXIST("ASSET WITH SAME SERIAL NO ALREADY EXIST!"),
    ;

    private final String template;

    GenericErrorCode(String template) {
        this.template = template;
    }

    public static GenericErrorCode getCode(String template) {
        for (GenericErrorCode code : GenericErrorCode.values()) {
            if (StringUtils.equals(template, template))
                return code;
        }
        return null;
    }

    public String getTemplate() {
        return this.template;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}