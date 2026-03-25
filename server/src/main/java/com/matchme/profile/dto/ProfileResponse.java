package com.matchme.profile.dto;

public class ProfileResponse {
    public Long userId;
    public String displayName;
    public String aboutMe;
    public String profilePictureUrl;
    public String location;

    public ProfileResponse(Long userId, String displayName, String aboutMe,
                           String profilePictureUrl, String location) {
        this.userId = userId;
        this.displayName = displayName;
        this.aboutMe = aboutMe;
        this.profilePictureUrl = profilePictureUrl;
        this.location = location;
    }
}
