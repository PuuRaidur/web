package com.matchme.user.dto;

public class MeResponse {
    public Long id;
    public String email;
    public String profileImageUrl;

    public MeResponse(Long id, String email, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
