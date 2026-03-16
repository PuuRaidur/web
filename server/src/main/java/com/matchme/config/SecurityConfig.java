package com.matchme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


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

                // public endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allows /auth/** without login.
                        .requestMatchers("/auth/**").permitAll()
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
}
