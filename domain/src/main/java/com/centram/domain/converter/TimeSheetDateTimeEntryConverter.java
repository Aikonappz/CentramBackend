package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Converter(autoApply = true)
public class TimeSheetDateTimeEntryConverter implements AttributeConverter<Map<LocalDate, LocalTime>, String> {

    private ObjectMapper objectMapper;
    private String dateFormat = "yyyy-MM-dd";
    private String timeFormat = "HH:mm";

    public TimeSheetDateTimeEntryConverter() {
        objectMapper = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .simpleDateFormat(dateFormat)
                .serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(timeFormat)))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(timeFormat)))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)))
                .build()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(Map<LocalDate, LocalTime> localDateLocalTimeMap) {
        String jsonString = null;
        if (!CollectionUtils.isEmpty(localDateLocalTimeMap)) {
            try {
                jsonString = objectMapper.writeValueAsString(localDateLocalTimeMap);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public Map<LocalDate, LocalTime> convertToEntityAttribute(String s) {
        Map<LocalDate, LocalTime> timeEntries = null;
        if (!StringUtils.isBlank(s)) {
            try {
                timeEntries = objectMapper.readValue(s, new TypeReference<Map<LocalDate, LocalTime>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return timeEntries;
    }
}
