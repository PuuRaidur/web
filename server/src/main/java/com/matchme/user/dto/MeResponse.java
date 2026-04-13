package com.matchme.user.dto;

public class MeResponse {
    public Long id;
    public String email;
    public String name;
    public String profilePictureUrl;

    public MeResponse(Long id, String email, String name, String profilePictureUrl) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }
}
