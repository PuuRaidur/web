package com.matchme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import com.matchme.auth.JwtAuthFilter;
import org.springframework.context.annotation.Configuration;

// makes Spring load this class
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // We inject JwtAuthFilter so we can attach it to the security chain.
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // disable CSRF because we use stateless JWT auth
                .csrf(csrf -> csrf.disable())
                // allow browser-based clients (Vite) to call the API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // public endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight requests
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Allows /auth/** without login.
                        .requestMatchers("/auth/**","/error", "/uploads/**").permitAll()
                        // Everything else needs JWT.
                        .anyRequest().authenticated()
                )

                // Disable session creation; we use JWT instead
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // insert our JWT filter before the default auth filter
                // Adds our JWT filter so Spring knows who is authenticated.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Provides PasswordEncoder bean required by AuthService
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS settings for local development (Vite on port 5173)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
