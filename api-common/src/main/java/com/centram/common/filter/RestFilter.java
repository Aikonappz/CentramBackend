package com.centram.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RestFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RestFilter.class);

    private List<String> allowedOrigins = new ArrayList<String>();

    public RestFilter() {

    }

    public RestFilter(String[] appAllowedOrigins) {
        allowedOrigins = Arrays.asList(appAllowedOrigins);
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        RequestWrapper requestWrapper = new RequestWrapper(req);
        //MDC.put("correlation-id", Utility.getCorrelationId());
        log.info("ORIGIN => {} ", req.getHeader("Origin"));
        log.info("ALLOWED ORIGIN => {} ", allowedOrigins);
        log.info("CONDITIONAL ALLOWED ORIGIN => {}", allowedOrigins.contains(req.getHeader("Origin")) ? req.getHeader("Origin") : "");
        res.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(req.getHeader("Origin")) ? req.getHeader("Origin") : "");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        //res.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        //res.setHeader("Access-Control-Allow-Credentials", "false");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Expose-Headers", "Authorization");
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me");
        filterChain.doFilter(requestWrapper, res);
    }
}
