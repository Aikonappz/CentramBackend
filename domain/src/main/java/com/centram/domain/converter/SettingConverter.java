package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.Setting;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter(autoApply = true)
@Component
public class SettingConverter implements AttributeConverter<Setting, String> {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        SettingConverter.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(Setting setting) {
        String jsonString = null;
        if (setting != null) {
            try {
                jsonString = SettingConverter.objectMapper.writeValueAsString(setting);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public Setting convertToEntityAttribute(String s) {
        Setting setting = null;
        if (!StringUtils.isBlank(s)) {
            try {
                setting = SettingConverter.objectMapper.readValue(s, Setting.class);
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return setting;
    }
}
