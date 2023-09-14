package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.UATRemark;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;

@Converter(autoApply = true)
public class UATRemarkConverter implements AttributeConverter<LinkedHashSet<UATRemark>, String> {

    private final ObjectMapper objectMapper;


    private final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    public UATRemarkConverter() {
        objectMapper = new Jackson2ObjectMapperBuilder().indentOutput(true).serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat).withZone(ZoneId.systemDefault());
                String s = localDateTime.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
                jsonGenerator.writeString(s);
            }
        }).deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat).withZone(ZoneId.systemDefault());
                String str = jsonParser.getText();
                LocalDateTime localDateTime = null;
                try {
                    localDateTime = LocalDateTime.parse(str, DATE_TIME_FORMATTER);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
                return localDateTime;
            }
        }).build().registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(LinkedHashSet<UATRemark> addresses) {
        String jsonString = null;
        if (!CollectionUtils.isEmpty(addresses)) {
            try {
                jsonString = objectMapper.writeValueAsString(addresses);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public LinkedHashSet<UATRemark> convertToEntityAttribute(String s) {
        LinkedHashSet<UATRemark> remarks = null;
        if (s != null && !s.isEmpty()) {
            try {
                remarks = objectMapper.readValue(s, new TypeReference<LinkedHashSet<UATRemark>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return remarks;
    }
}
