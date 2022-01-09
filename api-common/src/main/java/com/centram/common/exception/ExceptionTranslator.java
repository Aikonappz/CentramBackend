package com.centram.common.exception;

import com.centram.common.exeception.AppException;
import com.centram.common.exeception.ClientError;
import com.centram.common.exeception.GenericErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * The Class ExceptionTranslator.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);


    /**
     * Handle business exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler({AppException.class})
    public ResponseEntity<ClientError> handleBusinessException(AppException ex, WebRequest request) {
        ClientError digest = new ClientError(ex.getCode().getCode(), ex.getCode().getTemplate());
        return new ResponseEntity<ClientError>(digest, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    /*@ExceptionHandler({Throwable.class})
    public ResponseEntity<ClientError> handleException(Throwable ex, WebRequest request) {
        LOG.error(ex.getMessage());
        ClientError digest = new ClientError(GenericErrorCode.UNKNOWN_ERROR.getCode(), ex.getMessage());
        return new ResponseEntity<ClientError>(digest, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ClientError> handleAllExceptions(Exception ex, WebRequest request) {
        LOG.error(ex.getMessage());
        ClientError digest = new ClientError(GenericErrorCode.UNKNOWN_ERROR.getCode(), GenericErrorCode.UNKNOWN_ERROR.getTemplate());
        return new ResponseEntity<ClientError>(digest, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest webRequest) {
        LOG.error(ex.getMessage());
        ClientError digest = new ClientError(GenericErrorCode.valueOf(ex.getMessage()).getCode(), GenericErrorCode.valueOf(ex.getMessage()).getTemplate());
        return new ResponseEntity<>(digest, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }
}

