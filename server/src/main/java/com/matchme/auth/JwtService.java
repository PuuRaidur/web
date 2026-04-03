package com.matchme.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    // Constructor. Spring injects the config values into parameters.
    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // Creates an HMAC key from the secret string.
        this.expirationMs = expirationMs; // Stores expiration time (milliseconds).
    }

    // Creates a JWT for a given user ID.
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        // Builds the JWT with subject = userId, timestamps, signs it, then returns it as a string.
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // Reads the user ID from a token.
    public Long parseUserId(String token) {

        // Verifies signature with the same secret key, parses the token, and gets the sub claim.
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        // Converts the subject string to Long and returns it.
        return Long.parseLong(subject);
    }
}
