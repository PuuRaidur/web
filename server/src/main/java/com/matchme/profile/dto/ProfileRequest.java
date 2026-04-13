package com.matchme.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProfileRequest {
    // Required display name
    @NotBlank
    public String displayName;

    // Optional fields
    @NotBlank
    public String aboutMe;
    public String profilePictureUrl;
    @NotBlank
    public String location;
    @NotNull
    @Positive
    public Integer preferredDistanceKm;
    public Double latitude;
    public Double longitude;
}
