package com.company.ems.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Cross-cutting request logging.
 *
 * Generates (or reuses, if the caller already supplied one) a correlation /
 * request id, places it in the SLF4J MDC so every log line emitted while
 * handling this request can be tied back to it (see logback-spring.xml),
 * echoes it back as a response header for client-side traceability, and logs
 * a single line at request start and request end with method, path, status
 * and duration - a standard pattern for tracing requests across a corporate
 * service mesh / log aggregation pipeline.
 */
@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        long startedAt = System.currentTimeMillis();
        try {
            log.info("--> {} {} (client={})", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startedAt;
            log.info("<-- {} {} status={} duration={}ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs);
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }
}
