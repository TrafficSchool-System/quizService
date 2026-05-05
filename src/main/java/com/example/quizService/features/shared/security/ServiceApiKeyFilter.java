package com.example.quizService.features.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * ServiceApiKeyFilter - Authenticate service-to-service calls
 * 
 * Purpose:
 * - Authenticate internal service-to-service communication
 * - Runs BEFORE GatewayHeaderAuthenticationFilter
 * - Creates INTERNAL_SERVICE authentication if valid API key
 * 
 * Flow:
 * 1. Check if request has "X-Internal-API-Key" header
 * 2. If YES and API key is CORRECT:
 * → Create "INTERNAL_SERVICE" authentication
 * → Set in SecurityContext
 * → Request passes without Gateway headers
 * 3. If NO or INCORRECT API key:
 * → Do nothing, let GatewayHeaderAuthenticationFilter handle
 * 
 * Usage:
 * - ExamService calls /api/quizzes/final-exam with X-Internal-API-Key
 * 
 * Production:
 * - API Key stored in environment variable (SERVICE_API_KEY)
 * - All services share SAME key
 * - Rotated regularly
 */
@Component
public class ServiceApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ServiceApiKeyFilter.class);

    /**
     * HTTP header name containing API key
     */
    private static final String API_KEY_HEADER = "X-Internal-API-Key";

    /**
     * Valid API key that services must send
     * Loaded from application.properties (service.api.key)
     */
    @Value("${service.api.key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Get API key from request header
        String apiKey = request.getHeader(API_KEY_HEADER);

        // Step 2: If API key exists in header
        if (apiKey != null && !apiKey.isEmpty()) {

            log.debug("🔑 API Key detected in request to: {}", request.getRequestURI());

            // Step 3: Validate API key
            if (apiKey.equals(validApiKey)) {

                log.debug("Valid API key - service-to-service auth for: {}", request.getRequestURI());

                // Step 4: Create special authentication for internal services
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        "INTERNAL_SERVICE",
                        null,
                        authorities);

                // Step 5: Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("   SecurityContext updated with INTERNAL_SERVICE authentication");

            } else {
                // Invalid API key
                log.warn("⚠️ INVALID API Key detected!");
                log.warn("   Request URI: {}", request.getRequestURI());
                log.warn("   Remote IP: {}", request.getRemoteAddr());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\": \"Unauthorized\", \"message\": \"Invalid API Key\"}");
                return;
            }
        } else {
            log.debug("No API Key in request - will check for Gateway headers");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        log.debug("ServiceApiKeyFilter initialized: header={} keyConfigured={}",
                API_KEY_HEADER, validApiKey != null ? "YES" : "NO");
    }
}
