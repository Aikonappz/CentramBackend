package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Converter(autoApply = true)
public class TimeSheetEntriesConverter implements AttributeConverter<Map<String, Float>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Float> map) {
        String jsonString = null;
        if (!CollectionUtils.isEmpty(map)) {
            try {
                jsonString = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public Map<String, Float> convertToEntityAttribute(String s) {
        Map<String, Float> timeSheetEntries = null;
        if(!StringUtils.isBlank(s)){
            try {
                timeSheetEntries = objectMapper.readValue(s, new TypeReference<Map<String, Float>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return timeSheetEntries;
    }
}
