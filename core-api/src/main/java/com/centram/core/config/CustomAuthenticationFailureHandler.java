package com.centram.core.config;

import com.centram.common.exeception.ClientError;
import com.centram.common.exeception.GenericErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ClientError digest = new ClientError(GenericErrorCode.LOGIN_FAILED.getCode(), GenericErrorCode.LOGIN_FAILED.getTemplate());
        response.getOutputStream().println(objectMapper.writeValueAsString(digest));
    }
}