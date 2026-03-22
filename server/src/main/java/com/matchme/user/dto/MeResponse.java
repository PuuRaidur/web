package com.matchme.user.dto;

public class MeResponse {
    public Long id;
    public String name;
    public String profileImageUrl;

    public MeResponse(Long id, String name, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
