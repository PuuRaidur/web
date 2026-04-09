package com.matchme.auth.dto;

public class AuthResponse {
    public Long userId;
    public String token;
    public AuthResponse(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
