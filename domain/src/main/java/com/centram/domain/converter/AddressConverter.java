package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.Address;
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
public class AddressConverter implements AttributeConverter<List<Address>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Address> addresses) {
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
    public List<Address> convertToEntityAttribute(String s) {
        List<Address> addresses = null;
        if (!StringUtils.isBlank(s)) {
            try {
                addresses = objectMapper.readValue(s, new TypeReference<List<Address>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return addresses;
    }
}
