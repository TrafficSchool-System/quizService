package com.example.quizService.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        /**
         * REFACTORED SECURITY ARCHITECTURE:
         * - Gateway validerar JWT (JwtAuthenticationGlobalFilter)
         * - Gateway sätter headers: X-User-Id, X-User-Email, X-User-Role
         * - QuizService läser headers (GatewayHeaderAuthenticationFilter)
         */
        @Autowired
        private GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http

                                // FÖRKLARING: Stäng av CSRF eftersom vi använder JWT (stateless)
                                .csrf(csrf -> csrf.disable())

                                // FÖRKLARING: Konfigurera vilka endpoints som behöver authentication
                                // ORDNING VIKTIGT: Mer specifika regler måste komma först!
                                .authorizeHttpRequests(authz -> authz

                                                // ==============================================
                                                // 🌐 PUBLIC ENDPOINTS - No authentication required
                                                // ==============================================
                                                .requestMatchers("/api/quizzes/images/**")
                                                .permitAll()

                                                // ==============================================
                                                // 🔒 ADMIN ENDPOINTS - Require ADMIN role
                                                // ==============================================
                                                .requestMatchers("/api/admin/quizzes/**")
                                                .hasRole("ADMIN")

                                                // ==============================================
                                                // 👤 USER QUIZ ENDPOINTS - Require USER role
                                                // ==============================================
                                                .requestMatchers("/api/quizzes/**")
                                                .hasRole("USER")

                                                // Allt annat blockera
                                                .anyRequest().denyAll())

                                // FÖRKLARING: Stateless sessions - vi använder JWT istället för server sessions
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // FÖRKLARING: Lägg till Gateway header filter som läser X-User-* headers
                                .addFilterBefore(gatewayHeaderAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                // FÖRKLARING: Hantera unauthorized requests
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, authException) -> {

                                                        // Returnera 401 Unauthorized med custom meddelande
                                                        response.setStatus(401);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Unauthorized\", \"message\": \"JWT token krävs för denna endpoint\"}");
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {

                                                        // Returnera 403 Forbidden när användaren saknar rätt roll
                                                        response.setStatus(403);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Forbidden\", \"message\": \"Du har inte behörighet att komma åt denna resurs\"}");
                                                }));

                return http.build();
        }
}
