package com.centram.domain.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class ModelConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> categoryIds) {
        return StringUtils.join(categoryIds, ',');
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return Arrays.asList(s.split(","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
