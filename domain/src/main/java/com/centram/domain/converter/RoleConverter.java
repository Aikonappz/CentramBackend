package com.centram.domain.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<List<BigInteger>, String> {

    @Override
    public String convertToDatabaseColumn(List<BigInteger> categoryIds) {
        return StringUtils.join(categoryIds, ',');
    }

    @Override
    public List<BigInteger> convertToEntityAttribute(String s) {
        return Arrays.asList(s.split(","))
                .stream()
                .map(String::trim)
                .map(m -> {
                    return BigInteger.valueOf(Long.valueOf(m));
                })
                .collect(Collectors.toList());
    }
}
