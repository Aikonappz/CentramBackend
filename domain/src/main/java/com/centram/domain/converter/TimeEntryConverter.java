package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.TimeEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
public class TimeEntryConverter implements AttributeConverter<List<TimeEntry>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TimeEntry> bankDetails) {
        String jsonString = null;
        if (!CollectionUtils.isEmpty(bankDetails)) {
            try {
                jsonString = objectMapper.writeValueAsString(bankDetails);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public List<TimeEntry> convertToEntityAttribute(String s) {
        List<TimeEntry> timeEntries = null;
        if (s != null && !StringUtils.isEmpty(s)) {
            try {
                timeEntries = objectMapper.readValue(s, new TypeReference<List<TimeEntry>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return timeEntries;
    }
}
