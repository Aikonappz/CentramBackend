package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.Holiday;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
@Component
public class HolidayConverter implements AttributeConverter<List<Holiday>, String> {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        HolidayConverter.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(List<Holiday> holidays) {
        String jsonString = null;
        if (!CollectionUtils.isEmpty(holidays)) {
            try {
                jsonString = HolidayConverter.objectMapper.writeValueAsString(holidays);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public List<Holiday> convertToEntityAttribute(String s) {
        List<Holiday> holidays = null;
        if (s != null && !StringUtils.isEmpty(s)) {
            try {
                holidays = HolidayConverter.objectMapper.readValue(s, new TypeReference<List<Holiday>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return holidays;
    }
}
