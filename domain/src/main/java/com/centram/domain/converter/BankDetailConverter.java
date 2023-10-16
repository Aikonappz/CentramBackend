package com.centram.domain.converter;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.domain.BankDetail;
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
public class BankDetailConverter implements AttributeConverter<List<BankDetail>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<BankDetail> bankDetails) {
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
    public List<BankDetail> convertToEntityAttribute(String s) {
        List<BankDetail> bankDetails = null;
        if (!StringUtils.isBlank(s)) {
            try {
                bankDetails = objectMapper.readValue(s, new TypeReference<List<BankDetail>>() {
                });
            } catch (IOException e) {
                throw new AppException(GenericErrorCode.JSON_PROCESS_EXCEPTION);
            }
        }
        return bankDetails;
    }
}
