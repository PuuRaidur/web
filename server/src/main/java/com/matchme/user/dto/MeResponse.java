package com.matchme.user.dto;

public class MeResponse {
    public Long id;
    public String name;
    public String profilePictureUrl;

    public MeResponse(Long id, String name, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }
}
