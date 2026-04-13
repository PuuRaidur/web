package com.matchme.auth;

public class AuthResult {
    public final Long userId;
    public final String token;

    public AuthResult(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
