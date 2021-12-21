package com.centram.common.exception;

import com.centram.common.exeception.ClientError;
import com.centram.common.exeception.GenericErrorCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class})
    public final ResponseEntity<ClientError> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ClientError digest = new ClientError(GenericErrorCode.UNAUTHORIZED.getCode(), GenericErrorCode.UNAUTHORIZED.getTemplate());
        return new ResponseEntity<>(digest, HttpStatus.UNAUTHORIZED);
    }
}
