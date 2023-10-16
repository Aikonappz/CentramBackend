package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.ContactPerson;
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
public class ContactPersonConverter implements AttributeConverter<List<ContactPerson>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ContactPerson> addresses) {
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
    public List<ContactPerson> convertToEntityAttribute(String s) {
        List<ContactPerson> contactPersons = null;
        if (!StringUtils.isBlank(s)) {
            try {
                contactPersons = objectMapper.readValue(s, new TypeReference<List<ContactPerson>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return contactPersons;
    }
}
