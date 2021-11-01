package com.erp.api.client.config;

import com.erp.common.dto.LoggedInUserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class FeignConfig {
    private static final Logger LOG = LoggerFactory.getLogger(FeignConfig.class);

    @Value("${date.time.format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String dateTimeFormat;

    @Value("${date.format:yyyy-MM-dd}")
    private String dateFormat;

    //@Value("${header.device}")
    //private String dateFormat;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null) {
                Authentication authentication = securityContext.getAuthentication();
                if (authentication != null && (authentication.getPrincipal() instanceof LoggedInUserDTO)) {
                    LoggedInUserDTO loggedInUserDTO = (LoggedInUserDTO) authentication.getPrincipal();
                    requestTemplate.header("Authorization", loggedInUserDTO.getAuthToken());
                    requestTemplate.header("sample1", "sample1");
                    String httpMethod = requestTemplate.path();
                }
            }
        };
    }

    @Bean
    public Decoder decoder() {
        HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper());
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(httpMessageConverter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .simpleDateFormat(dateFormat)
                .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)))
                .serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat).withZone(ZoneId.systemDefault());
                        String s = localDateTime.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
                        jsonGenerator.writeString(s);
                    }
                })
                .deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
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
                })
                .serializerByType(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
                    @Override
                    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat);
                        try {
                            String s = zonedDateTime.format(DATE_TIME_FORMATTER);
                            jsonGenerator.writeString(s);
                        } catch (DateTimeParseException e) {
                            System.err.println(e);
                            jsonGenerator.writeString("");
                        }
                    }
                })
                .deserializerByType(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
                    @Override
                    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        String str = jsonParser.getText();
                        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat);
                        try {
                            return ZonedDateTime.parse(str, DATE_TIME_FORMATTER);
                        } catch (DateTimeParseException e) {
                            System.err.println(e);
                            return null;
                        }
                    }
                }).build();
        objectMapper.addMixIn(Object.class, IgnoreHibernatePropertiesInJackson.class);
        //objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private abstract class IgnoreHibernatePropertiesInJackson {
    }

}
