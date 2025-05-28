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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * The Class ExceptionTranslator.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);
    StringTemplateResolver templateResolver = new StringTemplateResolver();
    TemplateEngine templateEngine = new TemplateEngine();
    Context context = new Context(Locale.ENGLISH);

    public ExceptionTranslator() {
        this.templateResolver.setTemplateMode(TemplateMode.TEXT);
        this.templateEngine.setTemplateResolver(this.templateResolver);
    }

    /**
     * Handle business exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler({AppException.class})
    public ResponseEntity<ClientError> handleBusinessException(AppException ex, WebRequest request) {
        LOG.error(" code {} , message {} ", ex.getCode(), ex.getMessage());
        context.setVariable("entity", ex.getContext().get("entity"));
        context.setVariable("sheet", ex.getContext().get("sheet"));
        context.setVariable("col", ex.getContext().get("col"));
        context.setVariable("row", ex.getContext().get("row"));
        context.setVariable("errorMessage", ex.getContext().get("errorMessage"));
        ClientError digest = new ClientError(ex.getCode().getCode(), templateEngine.process(ex.getCode().getTemplate(), context));
        return new ResponseEntity<ClientError>(digest, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<ClientError> handleAccessDeniedException(Exception ex, WebRequest webRequest) {
        LOG.error(ex.getMessage());
        String code = GenericErrorCode.UNAUTHORIZED.getCode();
        String msg = GenericErrorCode.UNAUTHORIZED.getTemplate();
        ClientError digest = null;
        try {
            if (ex.getMessage() != null) {
                code = GenericErrorCode.valueOf(ex.getMessage()).getCode();
                msg = GenericErrorCode.valueOf(ex.getMessage()).getTemplate();
                digest = new ClientError(code, msg);
                return new ResponseEntity<>(digest, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                digest = new ClientError(code, msg);
                return new ResponseEntity<>(digest, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Throwable e) {
            digest = new ClientError(code, msg);
            return new ResponseEntity<>(digest, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }
    }
}

