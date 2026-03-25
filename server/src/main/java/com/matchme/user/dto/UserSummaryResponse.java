package com.matchme.user.dto;

public class UserSummaryResponse {
    public Long id;
    public String name;
    public String profilePictureUrl;

    public UserSummaryResponse(Long id, String name, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }
}
