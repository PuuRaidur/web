package com.matchme.profile.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfileRequest {
    // Required display name
    @NotBlank
    public String displayName;

    // Optional fields
    public String aboutMe;
    public String profilePictureUrl;
    public String location;
}
