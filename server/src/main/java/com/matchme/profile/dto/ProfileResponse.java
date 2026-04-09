package com.matchme.profile.dto;

public class ProfileResponse {
    public Long userId;
    public String displayName;
    public String aboutMe;
    public String profilePictureUrl;
    public String location;
    public Integer preferredDistanceKm;
    public Double latitude;
    public Double longitude;

    public ProfileResponse(Long userId, String displayName, String aboutMe,
                           String profilePictureUrl, String location,
                           Integer preferredDistanceKm, Double latitude, Double longitude) {
        this.userId = userId;
        this.displayName = displayName;
        this.aboutMe = aboutMe;
        this.profilePictureUrl = profilePictureUrl;
        this.location = location;
        this.preferredDistanceKm = preferredDistanceKm;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
