package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
public class ContactConverter implements AttributeConverter<List<Contact>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(List<Contact> contacts) {
        String jsonString = null;
        if (!CollectionUtils.isEmpty(contacts)) {
            try {
                jsonString = objectMapper.writeValueAsString(contacts);
            } catch (JsonProcessingException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return jsonString;
    }

    @Override
    public List<Contact> convertToEntityAttribute(String s) {
        List<Contact> contacts = null;
        if (!StringUtils.isBlank(s)) {
            try {
                contacts = objectMapper.readValue(s, new TypeReference<List<Contact>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return contacts;
    }
}
