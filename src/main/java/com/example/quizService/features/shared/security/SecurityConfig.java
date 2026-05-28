package com.example.quizService.features.shared.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Spring Security configuration
 * 
 * Architecture:
 * - Gateway validates JWT (JwtAuthenticationGlobalFilter)
 * - Gateway sets headers: X-User-Id, X-User-Email, X-User-Role
 * - QuizService reads headers (GatewayHeaderAuthenticationFilter)
 * - Internal services use X-Internal-API-Key (ServiceApiKeyFilter)
 * 
 * Filter Chain Order:
 * 1. ServiceApiKeyFilter - checks for X-Internal-API-Key header (internal
 * services)
 * 2. GatewayHeaderAuthenticationFilter - reads X-User-* headers (external users
 * via Gateway)
 * 
 * Endpoint Authorization:
 * - Public: None (all endpoints require authentication)
 * - User: /api/quizzes/** (requires USER or INTERNAL_SERVICE role)
 * - Admin: /api/admin/quizzes/** (requires ADMIN role)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter;

        @Autowired
        private ServiceApiKeyFilter serviceApiKeyFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // Disable CSRF (using JWT - stateless)
                                .csrf(csrf -> csrf.disable())

                                // Configure endpoint authorization
                                // ORDER MATTERS: More specific rules must come first!
                                .authorizeHttpRequests(authz -> authz

                                                // Health probe (Azure Container Apps)
                                                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                                                // ==============================================
                                                // 🔒 ADMIN ENDPOINTS - Require ADMIN role
                                                // ==============================================
                                                .requestMatchers("/api/admin/quizzes/**")
                                                .hasRole("ADMIN")

                                                // ==============================================
                                                // 👤 USER QUIZ ENDPOINTS - Require USER or INTERNAL_SERVICE role
                                                // ==============================================
                                                .requestMatchers("/api/quizzes/**")
                                                .hasAnyRole("USER", "INTERNAL_SERVICE")

                                                // Block everything else
                                                .anyRequest().denyAll())

                                // Stateless sessions (using JWT instead of server sessions)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Add filters in correct order
                                // 1. ServiceApiKeyFilter - checks for X-Internal-API-Key header
                                // 2. GatewayHeaderAuthenticationFilter - reads X-User-* headers
                                .addFilterBefore(serviceApiKeyFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(gatewayHeaderAuthenticationFilter,
                                                ServiceApiKeyFilter.class)

                                // Handle unauthorized requests
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, authException) -> {

                                                        // Return 401 Unauthorized with custom message
                                                        response.setStatus(401);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Unauthorized\", \"message\": \"JWT token required for this endpoint\"}");
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {

                                                        // Return 403 Forbidden when user lacks required role
                                                        response.setStatus(403);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Forbidden\", \"message\": \"You don't have permission to access this resource\"}");
                                                }));

                return http.build();
        }
}
