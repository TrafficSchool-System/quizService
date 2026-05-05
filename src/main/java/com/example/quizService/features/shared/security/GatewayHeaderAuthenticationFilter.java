package com.example.quizService.features.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * GatewayHeaderAuthenticationFilter - Read Gateway authentication headers
 * 
 * Architecture:
 * - Client → Gateway (validates JWT) → QuizService (reads headers)
 * - QuizService trusts headers from Gateway 100%
 * 
 * Flow:
 * 1. Gateway validates JWT token
 * 2. Gateway extracts userId, email, role from JWT
 * 3. Gateway sets headers: X-User-Id, X-User-Email, X-User-Role
 * 4. This filter reads headers and creates Spring Security Authentication
 * 
 * Security:
 * - These headers must NEVER reach QuizService directly from external clients
 * - Gateway must ALWAYS strip these headers from incoming requests
 * - Only Gateway is allowed to set these headers
 * 
 * Headers:
 * - X-User-Id: Long (userId from JWT)
 * - X-User-Email: String (email from JWT)
 * - X-User-Role: String (USER or ADMIN)
 */
@Component
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderAuthenticationFilter.class);

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader(USER_ID_HEADER);
        String userEmail = request.getHeader(USER_EMAIL_HEADER);
        String userRole = request.getHeader(USER_ROLE_HEADER);

        if (userId != null && userEmail != null && userRole != null) {

            log.debug("Gateway headers received userId={} role={}", userId, userRole);

            try {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole.toUpperCase());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        Collections.singletonList(authority));

                request.setAttribute("userId", Long.parseLong(userId));
                request.setAttribute("userEmail", userEmail);
                request.setAttribute("userRole", userRole);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Authentication set for userId={} role={}", userId, userRole);

            } catch (Exception e) {
                log.error("❌ Failed to create authentication from Gateway headers: {}", e.getMessage());
            }
        } else {
            log.debug("⏭️ No Gateway headers found - Request may be public");
        }

        filterChain.doFilter(request, response);
    }
}
