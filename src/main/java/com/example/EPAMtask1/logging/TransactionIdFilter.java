package com.example.EPAMtask1.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        logger.info("Incoming request: {} {}{}", method, uri, query != null ? "?" + query : "");

        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            int status = response.getStatus();
            logger.info("Completed request: {} {} -> status {}", method, uri, status);
            MDC.remove("transactionId");
        }
    }
}
