package com.centram.core.config;

import com.centram.common.exeception.ClientError;
import com.centram.common.exeception.GenericErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        ClientError digest = new ClientError(GenericErrorCode.LOGIN_FAILED.getCode(), GenericErrorCode.LOGIN_FAILED.getTemplate());
        objectMapper.registerModule(new JavaTimeModule());
        response.getOutputStream().println(objectMapper.writeValueAsString(digest));
    }
}
