package com.matchme.auth;

import com.matchme.user.User;
import com.matchme.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// It checks every request for a JWT in the Authorization header.
// If the token is valid, it sets the authenticated user in Spring Security.
@Component // @Component lets Spring auto‑register it.

// OncePerRequestFilter ensures the filter runs once per request.
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // We inject JwtService to parse tokens, and UserRepository to confirm the user exists.
    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // Add the method signature that Spring calls for every request.
    @Override
    // doFilterInternal is where we inspect the request.
    protected void doFilterInternal(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain
    ) throws jakarta.servlet.ServletException, java.io.IOException {

        // Read the Authorization header
        String authHeader = request.getHeader("Authorization"); // JWTs are sent as Authorization: Bearer <token>, so we need that header first.

        // Only proceed if header starts with "Bearer"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token after "Bearer"
            String token = authHeader.substring(7); // substring(7) skips "Bearer " (7 characters).

            // parse user id from the token and look up the user.
            try {
                // parse user id from the JWT
                Long userId = jwtService.parseUserId(token); // parseUserId validates the token signature and extracts the subject.

                // We check DB so deleted users can’t authenticate.
                // ensure the user still exists in the database
                var user = userRepository.findById(userId).orElse(null);

                if (user != null) {

                    // Create an authenticated principal with the user id
                    var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            userId, null, java.util.List.of() // We use userId as the principal so later endpoints can read it.
                    );
                    // This marks the request as authenticated.
                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);

                }
            } catch (Exception ignored) {
                // if token is valid, do nothing (request stays unauthenticated
                // Exceptions mean “invalid token,” so we just skip authentication.
            }

        }

        // filterChain.doFilter(...) passes control to the next filter (and eventually to controllers).
        filterChain.doFilter(request, response);
    }
}

