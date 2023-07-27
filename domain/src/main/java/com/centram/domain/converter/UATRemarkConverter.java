package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.UATRemark;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
public class UATRemarkConverter implements AttributeConverter<List<UATRemark>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<UATRemark> addresses) {
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
    public List<UATRemark> convertToEntityAttribute(String s) {
        List<UATRemark> remarks = null;
        if (s != null && !s.isEmpty()) {
            try {
                remarks = objectMapper.readValue(s, new TypeReference<List<UATRemark>>() {});
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return remarks;
    }
}
